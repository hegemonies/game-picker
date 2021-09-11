FROM python:3.8 as builder

WORKDIR /app

# true way
# COPY game_scrapper.py /app
# COPY requirements.txt /app

# develop way
COPY . /app

RUN pip3 install -r requirements.txt

FROM builder

CMD python3 game_scrapper.py
