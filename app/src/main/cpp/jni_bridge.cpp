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

#include <jni.h>
#include <oboe/Oboe.h>
#include "LearnEqEngine.h"
#include "logging_macros.h"
#include <android/asset_manager_jni.h>

extern "C" {

/**
 * Creates the audio engine
 *
 * @return a pointer to the audio engine. This should be passed to other methods
 */
JNIEXPORT jlong JNICALL
Java_lv_kartishev_eq_PlaybackEngine_native_1createEngine(
        JNIEnv *env,
        jclass /*unused*/,
        jobject jAssetManager) {
    // We use std::nothrow so `new` returns a nullptr if the engine creation fails

    AAssetManager *assetManager = AAssetManager_fromJava(env, jAssetManager);
    if (assetManager == nullptr) {
        LOGE("Could not obtain the AAssetManager");
    }

    LearnEqEngine *engine = new(std::nothrow) LearnEqEngine(*assetManager);
    return reinterpret_cast<jlong>(engine);
}

JNIEXPORT void JNICALL
Java_lv_kartishev_eq_PlaybackEngine_native_1deleteEngine(
        JNIEnv *env,
        jclass,
        jlong engineHandle) {

    delete reinterpret_cast<LearnEqEngine *>(engineHandle);
}

JNIEXPORT void JNICALL
Java_lv_kartishev_eq_PlaybackEngine_native_1setToneOn(
        JNIEnv *env,
        jclass,
        jlong engineHandle,
        jboolean isToneOn) {

    LearnEqEngine *engine = reinterpret_cast<LearnEqEngine *>(engineHandle);
    if (engine == nullptr) {
        LOGE("Engine handle is invalid, call createHandle() to create a new one");
        return;
    }
    engine->tap(isToneOn);
}

JNIEXPORT void JNICALL
Java_lv_kartishev_eq_PlaybackEngine_native_1changeEQ(
        JNIEnv *env,
        jclass,
        jlong engineHandle) {

    LearnEqEngine *engine = reinterpret_cast<LearnEqEngine *>(engineHandle);
    if (engine == nullptr) {
        LOGE("Engine handle is invalid, call createHandle() to create a new one");
        return;
    }
    engine->changeEQ();
}

JNIEXPORT jfloat JNICALL
Java_lv_kartishev_eq_PlaybackEngine_native_1getFrequency(
        JNIEnv *env,
        jclass,
        jlong engineHandle) {

    LearnEqEngine *engine = reinterpret_cast<LearnEqEngine *>(engineHandle);
    if (engine == nullptr) {
        LOGE("Engine handle is invalid, call createHandle() to create a new one");
        return 0.0;
    }
    return engine->getFrequency();
}

JNIEXPORT void JNICALL
Java_lv_kartishev_eq_PlaybackEngine_native_1setEQ(
        JNIEnv *env,
        jclass,
        jlong engineHandle,
        jboolean isEqOn) {

    LearnEqEngine *engine = reinterpret_cast<LearnEqEngine *>(engineHandle);
    if (engine == nullptr) {
        LOGE("Engine handle is invalid, call createHandle() to create a new one");
        return;
    }
    engine->setEQ(isEqOn);
}

JNIEXPORT void JNICALL
Java_lv_kartishev_eq_PlaybackEngine_native_1setDefaultStreamValues(JNIEnv *env,
                                                                                  jclass type,
                                                                                  jint sampleRate,
                                                                                  jint framesPerBurst) {
    oboe::DefaultStreamValues::SampleRate = (int32_t) sampleRate;
    oboe::DefaultStreamValues::FramesPerBurst = (int32_t) framesPerBurst;
}
} // extern "C"
