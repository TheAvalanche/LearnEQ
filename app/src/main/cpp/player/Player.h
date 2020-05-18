/*
 * Copyright 2018 The Android Open Source Project
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

#ifndef RHYTHMGAME_SOUNDRECORDING_H
#define RHYTHMGAME_SOUNDRECORDING_H

#include <cstdint>
#include <array>
#include <vector>

#include <chrono>
#include <memory>
#include <atomic>

#include <android/asset_manager.h>
#include <IRenderableAudio.h>

#include "../asset/DataSource.h"
#include "../util/constants.h"

class Player : public IRenderableAudio{

public:
    /**
     * Construct a new Player from the given DataSource. Players can share the same data source.
     * For example, you could play two identical sounds concurrently by creating 2 Players with the
     * same data source.
     *
     * @param source
     */
    Player(std::shared_ptr<DataSource> source)
        : mSource(source)
    {
        srand(static_cast<unsigned int>(time(NULL)));
        sample_rate = mSource->getProperties().sampleRate;
        reconfigure(freq[rand() % freq.size()]);
    };

    void renderAudio(float *targetData, int32_t numFrames);
    void resetPlayHead() { mReadFrameIndex = 0; };
    void setPlaying(bool isPlaying) { mIsPlaying = isPlaying; resetPlayHead(); };
    void setEQ(bool isEqOn) { mEqOn = isEqOn; };
    void changeEQ() { reconfigure(freq[rand() % freq.size()]); };
    void setLooping(bool isLooping) { mIsLooping = isLooping; };
    void setSource(std::shared_ptr<DataSource> source) { mSource = source; };
    float getFrequency() {
        return center_freq;
    }

private:
    int32_t mReadFrameIndex = 0;
    std::atomic<bool> mIsPlaying { false };
    std::atomic<bool> mEqOn { true };
    std::atomic<bool> mIsLooping { true };
    std::shared_ptr<DataSource> mSource;

    float a0, a1, a2, b0, b1, b2;
    float x1, x2, y, y1, y2;
    float gain_abs;
    eqType type = PEAK;
    float center_freq, Q = 1.0, gainDB = 12.0;
    int32_t sample_rate;

    void renderSilence(float*, int32_t);
    void reconfigure(float cf);

};

#endif //RHYTHMGAME_SOUNDRECORDING_H
