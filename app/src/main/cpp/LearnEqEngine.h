/*
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

#ifndef OBOE_HELLO_OBOE_ENGINE_H
#define OBOE_HELLO_OBOE_ENGINE_H

#include <oboe/Oboe.h>
#include <DefaultAudioStreamCallback.h>

#include "IRestartable.h"
#include "player/Player.h"
#include "asset/AAssetDataSource.h"

constexpr int32_t kBufferSizeAutomatic = 0;

class LearnEqEngine : public IRestartable {

public:
    explicit LearnEqEngine(AAssetManager&);

    virtual ~LearnEqEngine() = default;

    void tap(bool isDown);

    void setEQ(bool isEqOn);

    // From IRestartable
    void restart() override;

private:
    AAssetManager& mAssetManager;
    oboe::ManagedStream mStream;
    std::unique_ptr<DefaultAudioStreamCallback> mLatencyCallback;
    std::shared_ptr<Player> mBackingTrack;

    oboe::Result createPlaybackStream(oboe::AudioStreamBuilder builder);
    void start();
};

#endif //OBOE_HELLO_OBOE_ENGINE_H
