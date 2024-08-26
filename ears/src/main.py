import logging
from threading import Thread
from ears import AudioStreamer
from model import WhisperModel
from interface import EarsInterface
import tkinter as tk

logging.basicConfig(level=logging.INFO)


model = WhisperModel()
ears = AudioStreamer()

ears_worker = Thread(target=ears.start, args=(model,))
ears_worker.start()


root = tk.Tk()
interface = EarsInterface(root, ears)
root.mainloop()

ears_worker.join()