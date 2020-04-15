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

#ifndef SAMPLES_GAMECONSTANTS_H
#define SAMPLES_GAMECONSTANTS_H

// Filename for the backing track asset (in assets folder)
constexpr char kBackingTrackFilename[] { "firebird_48.mp3" };

constexpr float freq[] = {100, 200, 300, 400, 500, 600, 700, 800, 900, 1000, 1500, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000, 11000, 12000};

enum eqType {
    LOWPASS, HIGHPASS, BANDPASS, PEAK, NOTCH, LOWSHELF, HIGHSHELF
};


struct AudioProperties {
    int32_t channelCount;
    int32_t sampleRate;
};

#endif //SAMPLES_GAMECONSTANTS_H