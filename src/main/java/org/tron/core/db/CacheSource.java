package org.tron.core.db;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import org.iq80.leveldb.WriteOptions;
import org.tron.common.storage.SourceInter;
import org.tron.common.utils.Sha256Hash;

public class CacheSource implements SourceInter<byte[], byte[]> {

  @Getter
  private LoadingCache<Sha256Hash, Boolean> transactionStoreIdCache = CacheBuilder.newBuilder()
      .expireAfterAccess(2, TimeUnit.DAYS).recordStats()
      .build(new CacheLoader<Sha256Hash, Boolean>() {
        @Override
        public Boolean load(Sha256Hash key) {
          return Boolean.valueOf(false);
        }
      });

  @Override
  public void putData(byte[] key, byte[] val) {
    transactionStoreIdCache.put(Sha256Hash.of(key), true);
  }

  @Override
  public void putData(byte[] bytes, byte[] bytes2, WriteOptions options) {

  }

  @Override
  public byte[] getData(byte[] key) {
    if (transactionStoreIdCache.getUnchecked(Sha256Hash.of(key))) {
      return new byte[0];
    } else {
      return null;
    }
  }

  @Override
  public void deleteData(byte[] key) {
    transactionStoreIdCache.put(Sha256Hash.of(key), false);
  }

  @Override
  public void deleteData(byte[] bytes, WriteOptions options) {

  }

  @Override
  public boolean flush() {
    return false;
  }

  public boolean has(Sha256Hash transactionId) {
    return transactionStoreIdCache.getUnchecked(transactionId);
  }
}
