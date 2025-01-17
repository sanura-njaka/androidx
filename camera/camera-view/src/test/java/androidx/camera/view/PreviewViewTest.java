/*
 * Copyright 2021 The Android Open Source Project
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

package androidx.camera.view;

import static com.google.common.truth.Truth.assertThat;

import android.os.Build;
import android.util.Size;
import android.widget.FrameLayout;

import androidx.camera.core.CameraInfo;
import androidx.camera.core.SurfaceRequest;
import androidx.camera.core.impl.utils.executor.CameraXExecutors;
import androidx.camera.testing.fakes.FakeCamera;
import androidx.camera.testing.fakes.FakeCameraInfoInternal;
import androidx.camera.view.internal.compat.quirk.QuirkInjector;
import androidx.camera.view.internal.compat.quirk.SurfaceViewNotCroppedByParentQuirk;
import androidx.camera.view.internal.compat.quirk.SurfaceViewStretchedQuirk;
import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.internal.DoNotInstrument;

@RunWith(RobolectricTestRunner.class)
@DoNotInstrument
@Config(minSdk = Build.VERSION_CODES.LOLLIPOP)
public class PreviewViewTest {

    @After
    public void tearDown() {
        QuirkInjector.clear();
    }

    @Test
    @Config(minSdk = Build.VERSION_CODES.N_MR1)
    public void surfaceViewNormal_useSurfaceView() {
        // Assert: SurfaceView is used.
        assertThat(PreviewView.shouldUseTextureView(
                createSurfaceRequestCompatibleWithSurfaceView(),
                PreviewView.ImplementationMode.PERFORMANCE)).isFalse();
    }

    @Test
    public void surfaceViewStretchedQuirk_useTextureView() {
        // Arrange:
        QuirkInjector.inject(new SurfaceViewStretchedQuirk());

        // Assert: TextureView is used even the SurfaceRequest is compatible with SurfaceView.
        assertThat(PreviewView.shouldUseTextureView(
                createSurfaceRequestCompatibleWithSurfaceView(),
                PreviewView.ImplementationMode.PERFORMANCE)).isTrue();
    }

    @Test
    public void surfaceViewNotCroppedQuirk_useTextureView() {
        // Arrange:
        QuirkInjector.inject(new SurfaceViewNotCroppedByParentQuirk());

        // Assert: TextureView is used even the SurfaceRequest is compatible with SurfaceView.
        assertThat(PreviewView.shouldUseTextureView(
                createSurfaceRequestCompatibleWithSurfaceView(),
                PreviewView.ImplementationMode.PERFORMANCE)).isTrue();
    }

    @Test
    @Config(minSdk = Build.VERSION_CODES.N_MR1)
    public void surfaceView_reuseImplementation() {
        // Arrange:
        FrameLayout parent = new FrameLayout(ApplicationProvider.getApplicationContext());
        PreviewTransformation transformation = new PreviewTransformation();

        // Assert: The implementation should be reused if it is a SurfaceViewImplementation and
        //         other requirements are compatible with SurfaceView.
        assertThat(PreviewView.shouldReuseImplementation(
                new SurfaceViewImplementation(parent, transformation),
                createSurfaceRequestCompatibleWithSurfaceView(),
                PreviewView.ImplementationMode.PERFORMANCE)).isTrue();
    }

    @Test
    @Config(minSdk = Build.VERSION_CODES.N_MR1)
    public void surfaceView_notReuseImplementation_whenNull() {
        // Assert: The implementation should not be reused if it is null.
        assertThat(PreviewView.shouldReuseImplementation(
                null,
                createSurfaceRequestCompatibleWithSurfaceView(),
                PreviewView.ImplementationMode.PERFORMANCE)).isFalse();
    }

    @Test
    public void textureView_notReuseImplementation() {
        // Arrange:
        FrameLayout parent = new FrameLayout(ApplicationProvider.getApplicationContext());
        PreviewTransformation transformation = new PreviewTransformation();

        // Assert: The implementation should not be reused if it is a TextureViewImplementation.
        assertThat(PreviewView.shouldReuseImplementation(
                new TextureViewImplementation(parent, transformation),
                createSurfaceRequestCompatibleWithSurfaceView(),
                PreviewView.ImplementationMode.COMPATIBLE)).isFalse();
    }

    @Test(expected = IllegalArgumentException.class)
    public void setFrameUpdateListener_afterPerformanceModeSet() {
        PreviewView previewView = new PreviewView(ApplicationProvider.getApplicationContext());
        previewView.setImplementationMode(PreviewView.ImplementationMode.PERFORMANCE);

        previewView.setFrameUpdateListener(CameraXExecutors.mainThreadExecutor(),
                (timestamp) -> {});
    }

    @Test(expected = IllegalArgumentException.class)
    public void setFrameUpdateListener_beforePerformanceModeSet() {
        PreviewView previewView = new PreviewView(ApplicationProvider.getApplicationContext());
        previewView.setFrameUpdateListener(CameraXExecutors.mainThreadExecutor(),
                (timestamp) -> {});
        previewView.setImplementationMode(PreviewView.ImplementationMode.PERFORMANCE);
    }

    private SurfaceRequest createSurfaceRequestCompatibleWithSurfaceView() {
        FakeCameraInfoInternal cameraInfoInternal = new FakeCameraInfoInternal();
        cameraInfoInternal.setImplementationType(CameraInfo.IMPLEMENTATION_TYPE_CAMERA2);
        return new SurfaceRequest(new Size(800, 600),
                new FakeCamera(null, cameraInfoInternal),
                /*isRGB8888Required*/ false, () -> {});
    }
}
