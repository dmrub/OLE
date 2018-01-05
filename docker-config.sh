############### Configuration ###############

APP_NAME=ole
IMAGE_PREFIX=${IMAGE_PREFIX:-ole}
WAR_FILE=$APP_NAME.war
BUILD_PREFIX=${BUILD_PREFIX:-$IMAGE_PREFIX}
IMAGE_TAG=${IMAGE_TAG:-latest}
IMAGE_NAME=${IMAGE_PREFIX}:${IMAGE_TAG}

JAVA_MAVEN_DEPS=(
    https://github.com/rmrschub/igraphstore
    https://github.com/rmrschub/flapjack.git
    https://github.com/rmrschub/sodalite.git
    https://github.com/rmrschub/serendipity-client.git
    https://github.com/rmrschub/LDrawParser.git
)

############# End Configuration #############
