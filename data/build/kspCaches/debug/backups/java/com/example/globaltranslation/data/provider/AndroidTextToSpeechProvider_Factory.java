package com.example.globaltranslation.data.provider;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class AndroidTextToSpeechProvider_Factory implements Factory<AndroidTextToSpeechProvider> {
  private final Provider<Context> contextProvider;

  private AndroidTextToSpeechProvider_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public AndroidTextToSpeechProvider get() {
    return newInstance(contextProvider.get());
  }

  public static AndroidTextToSpeechProvider_Factory create(Provider<Context> contextProvider) {
    return new AndroidTextToSpeechProvider_Factory(contextProvider);
  }

  public static AndroidTextToSpeechProvider newInstance(Context context) {
    return new AndroidTextToSpeechProvider(context);
  }
}
