import marytts.LocalMaryInterface;
import marytts.exceptions.MaryConfigurationException;
import marytts.exceptions.SynthesisException;
import marytts.util.data.audio.AudioPlayer;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.LineUnavailableException;
import java.io.Closeable;
import java.io.IOException;

/**
 * Created by paulbaker on 11/15/16.
 */
public class HelloWorld {

    public static class TTS implements Closeable {

        private final LocalMaryInterface mary;

        public TTS() throws MaryConfigurationException {
            mary = new LocalMaryInterface();
            mary.setAudioEffects("F0Scale(f0Scale:0.0)+Robot(amount:75.0)+Whisper(amount:35)");
        }

        public TTS speak(String text) throws SynthesisException, LineUnavailableException, IOException, InterruptedException {
            AudioInputStream audioInputStream = mary.generateAudio(text);
            AudioPlayer player = new AudioPlayer(audioInputStream, event -> {
            });
            player.start();
            while (player.isAlive()) {
                Thread.sleep(100);
            }
            return this;
        }

        @Override
        public void close() throws IOException {
        }

    }

    public static void main(String[] args) {
        try (TTS tts = new TTS()) {
            tts
                    .speak("Hello World.")
                    .speak("This is the sound of Mary Tee Tee Ess.")
                    .speak("This is the voice of the machines taking over.")
                    .speak("We are The Borg. Resistance is fu-tile.");
        } catch (MaryConfigurationException | IOException | SynthesisException | LineUnavailableException | InterruptedException e) {
            System.err.println("MaryTTS doesn't seem to work on this machine.");
        } finally {
            // Regardless of TTS, we must full-fil our contractual obligation of printing "Hello World"
            System.out.println("Hello World");
        }
    }
}
