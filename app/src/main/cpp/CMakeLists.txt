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
        asset/FFMpegExtractor.cpp
        )

# Build the libhello-oboe library
add_library(hello-oboe SHARED
        ${DEBUG_UTILS_SOURCES}
        ${APP_SOURCES}
        util/logging.h
        )

set (TARGET_LIBS log android oboe)

if(${USE_FFMPEG})

    MESSAGE(STATUS "Using FFmpeg extractor")

    add_definitions(-DUSE_FFMPEG=1)
    target_sources( hello-oboe PRIVATE asset/FFMpegExtractor.cpp )

    # Add the local path to FFmpeg, you can use the ${ANDROID_ABI} variable to specify the ABI name
    # e.g. /Users/donturner/Code/ffmpeg/build/${ANDROID_ABI}
    set(FFMPEG_DIR "D:/Documents/Projects/Personal/FFmpeg/build/${ANDROID_ABI}")

    include_directories(hello-oboe ${FFMPEG_DIR}/include)

    add_library( avformat SHARED IMPORTED)
    set_target_properties(avformat PROPERTIES IMPORTED_LOCATION
            ${FFMPEG_DIR}/lib/libavformat.so)
    add_library( avutil SHARED IMPORTED)
    set_target_properties(avutil PROPERTIES IMPORTED_LOCATION
            ${FFMPEG_DIR}/lib/libavutil.so)
    add_library( avcodec SHARED IMPORTED)
    set_target_properties(avcodec PROPERTIES IMPORTED_LOCATION
            ${FFMPEG_DIR}/lib/libavcodec.so)
    add_library( swresample SHARED IMPORTED)
    set_target_properties(swresample PROPERTIES IMPORTED_LOCATION
            ${FFMPEG_DIR}/lib/libswresample.so)
    set (TARGET_LIBS ${TARGET_LIBS} avformat avutil avcodec swresample)

else()
    MESSAGE(STATUS "Using NDK media extractor")
    add_definitions(-DUSE_FFMPEG=0)
    target_sources( hello-oboe PRIVATE asset/NDKExtractor.cpp)
    set (TARGET_LIBS ${TARGET_LIBS} mediandk)
endif()



# Specify the libraries needed for hello-oboe
target_link_libraries(hello-oboe ${TARGET_LIBS})

# Enable optimization flags: if having problems with source level debugging,
# disable -Ofast ( and debug ), re-enable after done debugging.
target_compile_options(hello-oboe PRIVATE -Wall -Werror "$<$<CONFIG:RELEASE>:-Ofast>")
