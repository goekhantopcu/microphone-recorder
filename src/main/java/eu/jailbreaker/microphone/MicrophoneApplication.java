package eu.jailbreaker.microphone;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class MicrophoneApplication {
    // record duration, in milliseconds
    private static final long RECORD_TIME = 10000;  // 1 minute

    // path of the wav file
    private final File endpoint = Paths.get("test.wav").toFile();

    // format of audio file
    private final AudioFileFormat.Type type = AudioFileFormat.Type.WAVE;

    // the line from which audio data is captured
    private TargetDataLine line;

    /**
     * Entry to run the program
     */
    public static void main(String[] args) {
        final MicrophoneApplication recorder = new MicrophoneApplication();
        // creates a new thread that waits for a specified
        // of time before stopping
        final Thread stopper = new Thread(() -> {
            try {
                Thread.sleep(RECORD_TIME);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            recorder.close();
        });
        stopper.start();
        // start recording
        recorder.start();
    }

    public AudioFormat getAudioFormat() {
        final float sampleRate = 48000;
        final int sampleSizeInBits = 16;
        final int channels = 2;
        final boolean signed = true;
        final boolean bigEndian = true;
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }

    /**
     * Starts the target data line to capute
     */
    void start() {
        try {
            AudioFormat format = getAudioFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            // checks if system supports the data line
            if (!AudioSystem.isLineSupported(info)) {
                System.err.println("Line not supported");
                System.exit(0);
            }
            this.line = (TargetDataLine) AudioSystem.getLine(info);
            this.line.open(format);
            this.line.start();   // start capturing
            System.out.println("Start capturing...");
            final AudioInputStream ais = new AudioInputStream(this.line);
            System.out.println("Start recording...");
            // start recording
            AudioSystem.write(ais, this.type, this.endpoint);
        } catch (LineUnavailableException | IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Closes the target data line to finish capturing and recording
     */
    public void close() {
        this.line.stop();
        this.line.close();
        System.out.println("Finished");
    }
}
