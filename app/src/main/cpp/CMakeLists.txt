cmake_minimum_required(VERSION 3.4.1)

### INCLUDE OBOE LIBRARY ###

# Set the path to the Oboe library directory
set (OBOE_DIR ../../../../../oboe)

# Add the Oboe library as a subproject. Since Oboe is an out-of-tree source library we must also
# specify a binary directory
add_subdirectory(${OBOE_DIR} ./oboe-bin)

# Include the Oboe headers and shared sample code
include_directories(${OBOE_DIR}/include ${OBOE_DIR}/samples/shared)

# Debug utilities
set (DEBUG_UTILS_PATH "${OBOE_DIR}/samples/debug-utils")
set (DEBUG_UTILS_SOURCES ${DEBUG_UTILS_PATH}/trace.cpp)
include_directories(${DEBUG_UTILS_PATH})


### END OBOE INCLUDE SECTION ###


# App specific sources
set (APP_SOURCES
        jni_bridge.cpp
        LearnEqEngine.cpp
        asset/AAssetDataSource.cpp
        player/Player.cpp
        asset/NDKExtractor.cpp
        )

# Build the libhello-oboe library
add_library(hello-oboe SHARED
        ${DEBUG_UTILS_SOURCES}
        ${APP_SOURCES}
        util/logging.h
        )

MESSAGE(STATUS "Using NDK media extractor")
target_sources( hello-oboe PRIVATE asset/NDKExtractor.cpp)

# Specify the libraries needed for hello-oboe
target_link_libraries(hello-oboe android log oboe mediandk)

# Enable optimization flags: if having problems with source level debugging,
# disable -Ofast ( and debug ), re-enable after done debugging.
target_compile_options(hello-oboe PRIVATE -Wall -Werror "$<$<CONFIG:RELEASE>:-Ofast>")