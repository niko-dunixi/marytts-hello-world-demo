import javax.speech.Central;
import javax.speech.EngineException;
import javax.speech.synthesis.SpeakableListener;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;
import javax.speech.synthesis.Voice;
import java.beans.PropertyVetoException;
import java.io.Closeable;
import java.io.IOException;
import java.util.Locale;

/**
 * Created by paulbaker on 11/15/16.
 */
public class HelloWorld {

    private static class NoVoicesException extends Exception {
        public NoVoicesException() {
            super("There are no voices available");
        }
    }

    public static class TTS implements Closeable {

        private final Synthesizer speaker;

        public TTS() throws EngineException, PropertyVetoException, NoVoicesException {
            // Initialize the synthesizer with values for TTS
            SynthesizerModeDesc synthDesc = new SynthesizerModeDesc(Locale.US);
            Central.registerEngineCentral("com.sun.speech.freetts.jsapi.FreeTTSEngineCentral");
            speaker = Central.createSynthesizer(synthDesc);
            speaker.allocate();
            // Get all available voices
            Voice[] voices = ((SynthesizerModeDesc) speaker.getEngineModeDesc()).getVoices();
            // If we have any voices, select the last (and most high quality) of them
            if (voices.length > 0) {
                speaker.getSynthesizerProperties().setVoice(voices[voices.length - 1]);
            } else {
                // No TTS voices found
                throw new NoVoicesException();
            }
        }

        public TTS speak(String text) throws InterruptedException {
            return speak(text, null);
        }

        public TTS speak(String text, SpeakableListener listener) throws InterruptedException {
            speaker.speakPlainText(text, null);
            speaker.waitEngineState(Synthesizer.QUEUE_EMPTY);
            return this;
        }

        @Override
        public void close() throws IOException {
            try {
                speaker.deallocate();
            } catch (EngineException e) {
                throw new IOException(e);
            }
        }
    }

    public static void main(String[] args) {
        int exitStatus = 0;
        // The code below internally looks up the freetts.voices property, so it must be set.
        System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
        try (TTS speaker = new TTS()) {
            // Didn't have more than the "Kevin" brand voice on my machine, which pronounces things... oddly.
            // Compensating with pseudo phonetic spelling
            speaker
                    .speak("Hello wer-old.")
                    .speak("This is the voice of a machine.");
        } catch (EngineException | InterruptedException | PropertyVetoException | NoVoicesException | IOException e) {
            // Anything goes wrong, the machine likely doesn't support
            System.err.println("TTS is not supported on this machine.");
            exitStatus++;
        }
        // Regardless of TTS or not, we must fullfill the original obligation and print "Hello World"
        System.out.println("Hello World");
        System.exit(exitStatus);
    }
}
