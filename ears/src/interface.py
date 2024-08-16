import math
import random
import tkinter as tk

from ears import AudioStreamer
import copy


class EarsInterface:
    def __init__(self, root, ears: AudioStreamer):
        self.root = root
        self.ears = ears
        self.root.title("Steve Ears")

        # Ensure the window is always rendered correctly (especially on macOS)
        self.root.geometry("800x800")

        # Center the window
        self.center_window()

        # Loading label
        self.label = tk.Label(root, text="", font=("Helvetica", 24))
        self.label.pack(pady=20)

        # Canvas setup
        self.canvas = tk.Canvas(root, width=200, height=200)
        self.canvas.pack()

        title_history = tk.Label(root, text="History:", font=("Helvetica", 16), wraplength=400, justify="left")
        title_history.pack(pady=20)
        self.sentence_history = tk.Label(root, text="", font=("Helvetica", 11), wraplength=400, justify="center")
        self.sentence_history.pack(pady=20)

        # Wait for talk setup
        self.circle_radius = 30
        self.max_radius = 40
        self.min_radius = 30
        self.growth_speed = 0.5
        self.growing = True
        self.circle = self.canvas.create_oval(
            100 - self.circle_radius,
            100 - self.circle_radius,
            100 + self.circle_radius,
            100 + self.circle_radius,
            fill="white",
            outline="",
        )

        # Listening setup
        self.bar_width = 10
        self.num_bars = 11
        self.bars = []
        for i in range(self.num_bars):
            x0 = i * (self.bar_width + 5) + 20
            y0 = 100
            x1 = x0 + self.bar_width
            y1 = y0 + 20
            bar = self.canvas.create_rectangle(x0, y0, x1, y1, fill="green", outline="")
            self.bars.append(bar)

        # Processing setup
        self.spinner_lines = []
        self.num_lines = 12
        self.angle_step = 360 / self.num_lines
        self.current_angle = 0
        self.line_length = 15
        for i in range(self.num_lines):
            angle_rad = (self.angle_step * i) * (3.1416 / 180)
            x0 = 100 + 20 * math.cos(angle_rad)
            y0 = 100 + 20 * math.sin(angle_rad)
            x1 = 100 + (20 + self.line_length) * math.cos(angle_rad)
            y1 = 100 + (20 + self.line_length) * math.sin(angle_rad)
            line = self.canvas.create_line(x0, y0, x1, y1, width=4, fill="gray")
            self.spinner_lines.append(line)

        # Start the loading animation
        self.animate_loading()

    def animate_loading(self):
        # Update the label with the next pattern character
        self.canvas.itemconfig(self.circle, state="hidden")
        for bar in self.bars:
            self.canvas.itemconfig(bar, state="hidden")
        for line in self.spinner_lines:
            self.canvas.itemconfig(line, state="hidden")

        if self.ears.computing:
            self.label.config(text="Computing ....")

            for i, line in enumerate(self.spinner_lines):
                self.canvas.itemconfig(line, state="normal")
                color = "gray"
                if i == int(self.current_angle / self.angle_step) % self.num_lines:
                    color = "black"
                self.canvas.itemconfig(line, fill=color)

            self.current_angle += self.angle_step
        elif self.ears.listening:
            self.label.config(text="Listening ....")

            for bar in self.bars:
                self.canvas.itemconfig(bar, state="normal")
                height = random.randint(10, 100)
                x0, y0, x1, _ = self.canvas.coords(bar)
                self.canvas.coords(bar, x0, 100 - height, x1, 100 + height)
        else:
            self.canvas.itemconfig(self.circle, state="normal")
            if self.growing:
                self.circle_radius += self.growth_speed
                if self.circle_radius >= self.max_radius:
                    self.growing = False
            else:
                self.circle_radius -= self.growth_speed
                if self.circle_radius <= self.min_radius:
                    self.growing = True

            # Update the circle's size
            self.canvas.coords(
                self.circle,
                100 - self.circle_radius,
                100 - self.circle_radius,
                100 + self.circle_radius,
                100 + self.circle_radius,
            )
            self.label.config(text="Waiting")

        history = copy.copy(self.ears.sentences)
        history.reverse()
        display_text = "\n\n".join(history)
        self.sentence_history.config(text=display_text)

        # Schedule the next update
        self.root.after(50, self.animate_loading)

    def center_window(self):
        window_width = 800
        window_height = 900
        screen_width = self.root.winfo_screenwidth()
        screen_height = self.root.winfo_screenheight()

        position_top = int(screen_height / 2 - window_height / 2)
        position_right = int(screen_width / 2 - window_width / 2)

        self.root.geometry(
            f"{window_width}x{window_height}+{position_right}+{position_top}"
        )
