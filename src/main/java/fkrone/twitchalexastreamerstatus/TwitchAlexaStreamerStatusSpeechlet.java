package fkrone.twitchalexastreamerstatus;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.*;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
import java.net.URL;

public class TwitchAlexaStreamerStatusSpeechlet implements Speechlet {

    // Logger for debugging
    private static final Logger LOGGER = LoggerFactory.getLogger(TwitchAlexaStreamerStatusSpeechlet.class);

    // Intent for requesting stream status
    private static final String STREAM_STATUS_INTENT = "StreamStatusIntent";

    // Standard Amazon intents
    private static final String AMAZON_HELP_INTENT = "AMAZON.HelpIntent";
    private static final String AMAZON_STOP_INTENT = "AMAZON.StopIntent";
    private static final String AMAZON_CANCEL_INTENT = "AMAZON.CancelIntent";

    private static final String ACCESS_URL = "https://api.twitch.tv/kraken/streams/";

    // Id of channel to retrieve status for.
    // See https://dev.twitch.tv/docs/v5/guides/using-the-twitch-api/ for how to retrieve it.
    private static final String CHANNEL_ID = "";

    // Client id for requests.
    // See https://blog.twitch.tv/client-id-required-for-kraken-api-calls-afbb8e95f843 for more details.
    private static final String CLIENT_ID = "";

    @Override
    public void onSessionStarted(SessionStartedRequest request, Session session) throws SpeechletException {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("onSessionStarted requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());
        }

        // nothing to do here
    }

    @Override
    public SpeechletResponse onLaunch(LaunchRequest request, Session session) throws SpeechletException {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("onLaunch requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());
        }

        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(
                "If you want to know if your favourite streamer is currently live just ask me whether he or she is currently streaming or not.");
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(speech);
        return SpeechletResponse.newAskResponse(speech, reprompt);
    }

    @Override
    public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("onIntent requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());
        }

        Intent intent = request.getIntent();
        if (intent == null) {
            throw new SpeechletException("Invalid Intent");
        }
        String intentName = intent.getName();

        SpeechletResponse response;

        switch (intentName) {
            case STREAM_STATUS_INTENT:
                response = getStreamResponse();
                break;
            case AMAZON_HELP_INTENT:
                response = getHelpResponse();
                break;
            case AMAZON_STOP_INTENT:
            case AMAZON_CANCEL_INTENT:
                response = getCancelResponse();
                break;
            default:
                throw new SpeechletException("Invalid Intent");
        }
        return response;
    }

    @Override
    public void onSessionEnded(SessionEndedRequest request, Session session) throws SpeechletException {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("onSessionEnded requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());
        }

        // nothing to do here
    }

    private SpeechletResponse getHelpResponse() {
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(
                "If you want to know if your favorite streamer is currently streaming on twitch just ask whether he or she is currently streaming or not.");
        return SpeechletResponse.newTellResponse(speech);
    }

    private SpeechletResponse getCancelResponse() {
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText("See you soon.");
        return SpeechletResponse.newTellResponse(speech);
    }

    private SpeechletResponse getStreamResponse() {
        try {
            URL url = new URL(ACCESS_URL + CHANNEL_ID);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("Client-ID", CLIENT_ID);
            con.setRequestProperty("Accept", "application/vnd.twitchtv.v5+json");

            PlainTextOutputSpeech speech = new PlainTextOutputSpeech();

            if (con.getResponseCode() != 200) {
                if (LOGGER.isErrorEnabled()) {
                    LOGGER.error("Got response code " + con.getResponseCode() + " from api.");
                }
                speech.setText("I'm sorry but it seems like I could not retrieve the stream status.");
                return SpeechletResponse.newTellResponse(speech);
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(con.getInputStream());

            if (!rootNode.isObject()) {
                LOGGER.error("Got invalid resonse from api.");
                speech.setText("I'm sorry but I could not figure out the status of the stream.");
                return SpeechletResponse.newTellResponse(speech);
            }

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Got valid response from api:\n" + rootNode.asText());
            }

            JsonNode streamNode = rootNode.get("stream");

            if (streamNode.isNull()) {
                LOGGER.debug("Streamer is currently not live.");
                speech.setText("The streamer you requested is currently not streaming at the moment.");
                return SpeechletResponse.newTellResponse(speech);
            }

            LOGGER.debug("Streamer is currently live.");

            if (!streamNode.isObject()) {
                LOGGER.error("Blame Twitch, something went horribly wrong. The stream object is not an object.");
                speech.setText("I'm sorry but there is no status of the stream at the moment.");
                return SpeechletResponse.newTellResponse(speech);
            }

            JsonNode channelNode = streamNode.get("channel");

            if (!channelNode.isObject()) {
                LOGGER.error("Blame Twitch, something went horribly wrong. The channel object is not an object.");
                speech.setText("I'm sorry but no information about the channel could be retrieved.");
                return SpeechletResponse.newTellResponse(speech);
            }

            String response =
                    channelNode.get("display_name").asText()
                    + " is currently streaming "
                    + streamNode.get("game")
                                .asText()
                    + " having "
                    + streamNode.get("viewers").asText()
                    + " viewers. The stream has the title: "
                    + channelNode.get("status").asText();

            speech.setText(response);

            return SpeechletResponse.newTellResponse(speech);
        } catch (Exception e) {
            LOGGER.error("Error retrieving stream status.", e);
            PlainTextOutputSpeech errorSpeech = new PlainTextOutputSpeech();
            errorSpeech.setText("An error occurred while retrieving stream status.");
            return SpeechletResponse.newTellResponse(errorSpeech);
        }
    }
}
