import whisper


class WhisperModel:
    def __init__(self) -> None:
        self.model = whisper.load_model("base", device="cpu")

    def transcribe(self, audio: str) -> str:
        return self.model.transcribe(audio, language="en", fp16=False, verbose=True)
