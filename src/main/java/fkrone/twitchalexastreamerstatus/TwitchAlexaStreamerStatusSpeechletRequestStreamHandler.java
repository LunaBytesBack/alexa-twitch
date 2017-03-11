package fkrone.twitchalexastreamerstatus;

import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;

import java.util.HashSet;
import java.util.Set;

public class TwitchAlexaStreamerStatusSpeechletRequestStreamHandler extends SpeechletRequestStreamHandler {

    private static final Set<String> SUPPORTED_APPLICATION_IDS;

    static {
        /*
         * This Id can be found on https://developer.amazon.com/edw/home.html#/ "Edit" the relevant
         * Alexa Skill and put the relevant Application Ids in this Set.
         */
        SUPPORTED_APPLICATION_IDS = new HashSet<>();
        SUPPORTED_APPLICATION_IDS.add("");
    }

    public TwitchAlexaStreamerStatusSpeechletRequestStreamHandler() {
        super(new TwitchAlexaStreamerStatusSpeechlet(), SUPPORTED_APPLICATION_IDS);
    }
}
