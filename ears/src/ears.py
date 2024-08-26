import logging
import wave

import numpy as np
import pyaudio
from model import WhisperModel

import requests


class AudioStreamer:
    def __init__(self) -> None:
        # Setup PyAudio
        self.chunk = 1024  # Record in chunks of 1024 samples
        self.sample_format = pyaudio.paInt16  # 16 bits per sample
        self.channels = 1
        self.fs = 16000  # Record at 16000 samples per second

        self.p = pyaudio.PyAudio()  # Create an interface to PortAudio

        # Parameters for detecting speech
        self.volume_threshold = 500  # Adjust based on your environment
        self.silence_threshold = 1.5  # Seconds of silence indicating end of sentence
        self.silence_chunks = int(self.silence_threshold * self.fs / self.chunk)

        self.listening = False
        self.computing = False
        self.sentences = []

    def is_silent(self, data):
        """Returns True if below the volume threshold"""
        audio_data = np.frombuffer(data, dtype=np.int16)
        return np.abs(audio_data).mean() < self.volume_threshold

    def start(self, model: WhisperModel) -> None:
        logging.info("Start listening...")

        stream = self.p.open(
            format=self.sample_format,
            channels=self.channels,
            rate=self.fs,
            frames_per_buffer=self.chunk,
            input=True,
        )

        while True:
            frames = []
            silent_chunks = 0
            self.listening = False
            self.computing = False

            while True:
                # Read the microphone data
                data = stream.read(self.chunk, exception_on_overflow=False)

                if not self.listening:
                    if not self.is_silent(data):
                        logging.info("Start of speech detected")
                        self.listening = True
                else:
                    frames.append(data)
                    if self.is_silent(data):
                        silent_chunks += 1
                        if silent_chunks > self.silence_chunks:
                            logging.info("End of speech detected")
                            break
                    else:
                        silent_chunks = 0

            self.computing = True
            # Save the captured frames to a file
            wf = wave.open("temp.wav", "wb")
            wf.setnchannels(self.channels)
            wf.setsampwidth(self.p.get_sample_size(self.sample_format))
            wf.setframerate(self.fs)
            wf.writeframes(b"".join(frames))
            wf.close()

            # Transcribe with Whisper
            result = model.transcribe("temp.wav")
            text = result["text"].strip()

            logging.info(f"Detected sentence: {text}")
            self.sentences.append(text)

            requests.get(f"http://localhost:6749/api/data?message={text}")
