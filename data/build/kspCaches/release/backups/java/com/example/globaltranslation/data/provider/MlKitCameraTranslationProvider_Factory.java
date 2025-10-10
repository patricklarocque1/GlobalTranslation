package com.example.globaltranslation.data.provider;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class MlKitCameraTranslationProvider_Factory implements Factory<MlKitCameraTranslationProvider> {
  private final Provider<MlKitTextRecognitionProvider> textRecognitionProvider;

  private final Provider<MlKitTranslationProvider> translationProvider;

  private MlKitCameraTranslationProvider_Factory(
      Provider<MlKitTextRecognitionProvider> textRecognitionProvider,
      Provider<MlKitTranslationProvider> translationProvider) {
    this.textRecognitionProvider = textRecognitionProvider;
    this.translationProvider = translationProvider;
  }

  @Override
  public MlKitCameraTranslationProvider get() {
    return newInstance(textRecognitionProvider.get(), translationProvider.get());
  }

  public static MlKitCameraTranslationProvider_Factory create(
      Provider<MlKitTextRecognitionProvider> textRecognitionProvider,
      Provider<MlKitTranslationProvider> translationProvider) {
    return new MlKitCameraTranslationProvider_Factory(textRecognitionProvider, translationProvider);
  }

  public static MlKitCameraTranslationProvider newInstance(
      MlKitTextRecognitionProvider textRecognitionProvider,
      MlKitTranslationProvider translationProvider) {
    return new MlKitCameraTranslationProvider(textRecognitionProvider, translationProvider);
  }
}
