# Setup

### 1. Install conda
https://conda.io/projects/conda/en/latest/user-guide/install/index.html

### 2. Install requirements
#### Linux
```bash
conda create python=3.10 -n SteveEars
conda activate SteveEars
apt install ffmpeg
pip install requirements.txt
```

#### MacOS
```bash
conda create python=3.10 -n SteveEars
conda activate SteveEars
brew install ffmpeg
pip install requirements.txt
```

#### Windows
Please install ffmpeg manually here: https://ffmpeg.org/download.html
```bash
conda create python=3.10 -n SteveEars
conda activate SteveEars
brew install ffmpeg
pip install requirements.txt
```

# 🚀 Launch

```bash
conda activate SteveEars
python src/main.py
```