FROM gradle:jdk11

ARG DEBIAN_FRONTEND=noninteractive
RUN apt update && apt upgrade -y

ARG ANDROID_HOME=/itlab_mobile
ENV ANDROID_HOME=${ANDROID_HOME}
WORKDIR ${ANDROID_HOME}

ENV ANDROID_SDK_DOWNLOAD_URL=https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip
RUN wget -O tools.zip ${ANDROID_SDK_DOWNLOAD_URL}
RUN sha256sum tools.zip > check_sum_file

ENV ANDROID_SDK_CHECKSUM=bd1aa17c7ef10066949c88dc6c9c8d536be27f992a1f3b5a584f9bd2ba5646a0
RUN echo ${ANDROID_SDK_CHECKSUM} >> check_sum_file
RUN cat check_sum_file
RUN sha256sum --check check_sum_file 2>&1
RUN rm -f check_sum_file

RUN unzip -d ${ANDROID_HOME} tools.zip
RUN rm -f tools.zip

RUN env
RUN ls -al

ENV PATH=${ANDROID_HOME}/cmdline-tools:${ANDROID_HOME}/cmdline-tools/bin:$PATH:${ANDROID_HOME}

RUN yes | sdkmanager --sdk_root=${ANDROID_HOME} --licenses

COPY . .
RUN apt install -y dos2unix
RUN dos2unix gradlew
