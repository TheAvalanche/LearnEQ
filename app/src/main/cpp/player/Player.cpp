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

#include "Player.h"
#include "../util/logging.h"
#include <cmath>

void Player::renderAudio(float *targetData, int32_t numFrames){

    const AudioProperties properties = mSource->getProperties();

    if (mIsPlaying){

        int64_t framesToRenderFromData = numFrames;
        int64_t totalSourceFrames = mSource->getSize() / properties.channelCount;
        const float *data = mSource->getData();

        // Check whether we're about to reach the end of the recording
        if (!mIsLooping && mReadFrameIndex + numFrames >= totalSourceFrames){
            framesToRenderFromData = totalSourceFrames - mReadFrameIndex;
            mIsPlaying = false;
        }

        for (int i = 0; i < framesToRenderFromData; ++i) {
            for (int j = 0; j < properties.channelCount; ++j) {

                const float in = data[(mReadFrameIndex * properties.channelCount) + j] * 0.9f; //reduce volume not to clip

                y = b0 * in + b1 * x1 + b2 * x2 - a1 * y1 - a2 * y2;
                x2 = x1;
                x1 = in;
                y2 = y1;
                y1 = y;

                if (mEqOn) {
                    targetData[(i * properties.channelCount) + j] = y;
                } else {
                    targetData[(i * properties.channelCount) + j] = in;
                }
            }

            // Increment and handle wraparound
            if (++mReadFrameIndex >= totalSourceFrames) mReadFrameIndex = 0;
        }

        if (framesToRenderFromData < numFrames){
            // fill the rest of the buffer with silence
            renderSilence(&targetData[framesToRenderFromData], numFrames * properties.channelCount);
        }

    } else {
        renderSilence(targetData, numFrames * properties.channelCount);
    }
}

void Player::reconfigure(float cf) {
    center_freq = cf;
    // only used for peaking and shelving filters
    gain_abs = pow(10, gainDB / 40);
    float omega = 2 * M_PI * cf / sample_rate;
    float sn = sin(omega);
    float cs = cos(omega);
    float alpha = sn / (2 * Q);
    float beta = sqrt(gain_abs + gain_abs);
    switch (type) {
        case BANDPASS:
            b0 = alpha;
            b1 = 0;
            b2 = -alpha;
            a0 = 1 + alpha;
            a1 = -2 * cs;
            a2 = 1 - alpha;
            break;
        case LOWPASS:
            b0 = (1 - cs) / 2;
            b1 = 1 - cs;
            b2 = (1 - cs) / 2;
            a0 = 1 + alpha;
            a1 = -2 * cs;
            a2 = 1 - alpha;
            break;
        case HIGHPASS:
            b0 = (1 + cs) / 2;
            b1 = -(1 + cs);
            b2 = (1 + cs) / 2;
            a0 = 1 + alpha;
            a1 = -2 * cs;
            a2 = 1 - alpha;
            break;
        case NOTCH:
            b0 = 1;
            b1 = -2 * cs;
            b2 = 1;
            a0 = 1 + alpha;
            a1 = -2 * cs;
            a2 = 1 - alpha;
            break;
        case PEAK:
            b0 = 1 + (alpha * gain_abs);
            b1 = -2 * cs;
            b2 = 1 - (alpha * gain_abs);
            a0 = 1 + (alpha / gain_abs);
            a1 = -2 * cs;
            a2 = 1 - (alpha / gain_abs);
            break;
        case LOWSHELF:
            b0 = gain_abs * ((gain_abs + 1) - (gain_abs - 1) * cs + beta * sn);
            b1 = 2 * gain_abs * ((gain_abs - 1) - (gain_abs + 1) * cs);
            b2 = gain_abs * ((gain_abs + 1) - (gain_abs - 1) * cs - beta * sn);
            a0 = (gain_abs + 1) + (gain_abs - 1) * cs + beta * sn;
            a1 = -2 * ((gain_abs - 1) + (gain_abs + 1) * cs);
            a2 = (gain_abs + 1) + (gain_abs - 1) * cs - beta * sn;
            break;
        case HIGHSHELF:
            b0 = gain_abs * ((gain_abs + 1) + (gain_abs - 1) * cs + beta * sn);
            b1 = -2 * gain_abs * ((gain_abs - 1) + (gain_abs + 1) * cs);
            b2 = gain_abs * ((gain_abs + 1) + (gain_abs - 1) * cs - beta * sn);
            a0 = (gain_abs + 1) - (gain_abs - 1) * cs + beta * sn;
            a1 = 2 * ((gain_abs - 1) - (gain_abs + 1) * cs);
            a2 = (gain_abs + 1) - (gain_abs - 1) * cs - beta * sn;
            break;
    }
    // prescale flter constants
    b0 /= a0;
    b1 /= a0;
    b2 /= a0;
    a1 /= a0;
    a2 /= a0;
}

void Player::renderSilence(float *start, int32_t numSamples){
    for (int i = 0; i < numSamples; ++i) {
        start[i] = 0;
    }
}