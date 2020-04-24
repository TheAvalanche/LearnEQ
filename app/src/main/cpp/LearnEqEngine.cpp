/**
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


#include <inttypes.h>
#include <memory>
#include <chrono>
#include <ctime>

#include <Oscillator.h>

#include "LearnEqEngine.h"
#include "util/constants.h"



/**
 * Main audio engine for the HelloOboe sample. It is responsible for:
 *
 * - Creating a callback object which is supplied when constructing the audio stream, and will be
 * called when the stream starts
 * - Restarting the stream when user-controllable properties (Audio API, channel count etc) are
 * changed, and when the stream is disconnected (e.g. when headphones are attached)
 * - Calculating the audio latency of the stream
 *
 */
LearnEqEngine::LearnEqEngine(AAssetManager &assetManager): mAssetManager(assetManager), mLatencyCallback(std::make_unique<DefaultAudioStreamCallback>(*this)) {
    start();
}

void LearnEqEngine::restart() {
    start();
}

void LearnEqEngine::start() {
    auto result = createPlaybackStream(oboe::AudioStreamBuilder());

    // Set the properties of our audio source(s) to match that of our audio stream
    AudioProperties targetProperties {
            .channelCount = mStream->getChannelCount(),
            .sampleRate = mStream->getSampleRate()
    };


    time_t my_time = time(NULL);

    // ctime() used to give the present time
    LOGE("test %s", ctime(&my_time));
    // Create a data source and player for our backing track
    std::shared_ptr<AAssetDataSource> backingTrackSource {
            AAssetDataSource::newFromCompressedAsset(mAssetManager, kBackingTrackFilename, targetProperties)
    };

    time_t my_time2 = time(NULL);

    // ctime() used to give the present time
    LOGE("test 2 %s", ctime(&my_time2));
    if (backingTrackSource == nullptr){
        LOGE("Could not load source data for backing track");
    }

    player = std::make_shared<Player>(backingTrackSource);

    player->setLooping(true);

    time_t my_time3 = time(NULL);
    LOGE("test 3 %s", ctime(&my_time3));

    if (result == oboe::Result::OK){
        time_t my_time4 = time(NULL);
        LOGE("test 4 %s", ctime(&my_time4));
        mLatencyCallback->setSource(std::dynamic_pointer_cast<IRenderableAudio>(player));
        time_t my_time5 = time(NULL);
        LOGE("test 5 %s", ctime(&my_time5));
        mStream->start();
        time_t my_time6 = time(NULL);
        LOGE("test 6 %s", ctime(&my_time6));
    } else {
        LOGE("Error creating playback stream. Error: %s", oboe::convertToText(result));
    }
}

void LearnEqEngine::tap(bool isDown) {
    player->setPlaying(isDown);
}

void LearnEqEngine::setEQ(bool isEqOn) {
    player->setEQ(isEqOn);
}

void LearnEqEngine::changeEQ() {
    player->changeEQ();
}

float LearnEqEngine::getFrequency() {
    return player->getFrequency();
}

oboe::Result LearnEqEngine::createPlaybackStream(oboe::AudioStreamBuilder builder) {
    return builder.setSharingMode(oboe::SharingMode::Exclusive)
            ->setPerformanceMode(oboe::PerformanceMode::LowLatency)
            ->setFormat(oboe::AudioFormat::Float)
            ->setCallback(mLatencyCallback.get())
            ->openManagedStream(mStream);
}

