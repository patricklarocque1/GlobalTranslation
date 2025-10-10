package com.example.globaltranslation.data.repository;

import com.example.globaltranslation.data.local.ConversationDao;
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
public final class RoomConversationRepository_Factory implements Factory<RoomConversationRepository> {
  private final Provider<ConversationDao> daoProvider;

  private RoomConversationRepository_Factory(Provider<ConversationDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public RoomConversationRepository get() {
    return newInstance(daoProvider.get());
  }

  public static RoomConversationRepository_Factory create(Provider<ConversationDao> daoProvider) {
    return new RoomConversationRepository_Factory(daoProvider);
  }

  public static RoomConversationRepository newInstance(ConversationDao dao) {
    return new RoomConversationRepository(dao);
  }
}
