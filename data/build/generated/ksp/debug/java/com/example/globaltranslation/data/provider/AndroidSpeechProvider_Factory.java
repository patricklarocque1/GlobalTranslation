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
public final class AndroidSpeechProvider_Factory implements Factory<AndroidSpeechProvider> {
  private final Provider<Context> contextProvider;

  private AndroidSpeechProvider_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public AndroidSpeechProvider get() {
    return newInstance(contextProvider.get());
  }

  public static AndroidSpeechProvider_Factory create(Provider<Context> contextProvider) {
    return new AndroidSpeechProvider_Factory(contextProvider);
  }

  public static AndroidSpeechProvider newInstance(Context context) {
    return new AndroidSpeechProvider(context);
  }
}
