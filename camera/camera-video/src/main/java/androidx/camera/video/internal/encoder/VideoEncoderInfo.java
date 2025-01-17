/*
 * Copyright 2022 The Android Open Source Project
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

package androidx.camera.video.internal.encoder;

import android.util.Range;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

/**
 * VideoEncoderInfo provides video encoder related information and capabilities.
 */
@RequiresApi(21) // TODO(b/200306659): Remove and replace with annotation on package-info.java
public interface VideoEncoderInfo extends EncoderInfo {
    /** Returns the range of supported video widths. */
    @NonNull
    Range<Integer> getSupportedWidths();

    /** Returns the range of supported video heights. */
    @NonNull
    Range<Integer> getSupportedHeights();

    /**
     * Returns the range of supported video widths for a video height.
     *
     * @throws IllegalArgumentException if height is not supported.
     * @see #getSupportedHeights()
     * @see #getHeightAlignment()
     */
    @NonNull
    Range<Integer> getSupportedWidthsFor(int height);

    /**
     * Returns the range of supported video heights for a video width.
     *
     * @throws IllegalArgumentException if width is not supported.
     * @see #getSupportedWidths()
     * @see #getWidthAlignment()
     */
    @NonNull
    Range<Integer> getSupportedHeightsFor(int width);

    /**
     * Returns the alignment requirement for video width (in pixels).
     *
     * <p>This is usually a power-of-2 value that video width must be a multiple of.
     */
    int getWidthAlignment();

    /**
     * Returns the alignment requirement for video height (in pixels).
     *
     * <p>This is usually a power-of-2 value that video height must be a multiple of.
     */
    int getHeightAlignment();
}
