package org.tron.core.db;

import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.tron.common.utils.Sha256Hash;
import org.tron.core.db.AbstractRevokingStore.RevokingTuple;

@Slf4j
@Component
public class CacheWithRevoking {

  private CacheSource cacheSource;
  private RevokingDatabase revokingDatabase;

  private CacheWithRevoking() {
    this.revokingDatabase = RevokingStore.getInstance();
    cacheSource = new CacheSource();
  }

  public void put(byte[] key) {
    if (Objects.isNull(key)) {
      return;
    }
    cacheSource.putData(key, key);
    onCreate(key);
  }

  public void delete(byte[] key) {
    onDelete(key);
    cacheSource.deleteData(key);
  }

  /**
   * This should be called just after an object is created
   */
  private void onCreate(byte[] key) {
    revokingDatabase.onCreate(new RevokingTuple(cacheSource, key), null);
  }

  /**
   * This should be called just before an object is modified
   */
  private void onModify(byte[] key, byte[] value) {
    revokingDatabase.onModify(new RevokingTuple(cacheSource, key), value);
  }

  /**
   * This should be called just before an object is removed.
   */
  private void onDelete(byte[] key) {
    byte[] value;
    if (Objects.nonNull(value = cacheSource.getData(key))) {
      revokingDatabase.onRemove(new RevokingTuple(cacheSource, key), value);
    }
  }

  public boolean has(Sha256Hash transactionId) {
    return cacheSource.has(transactionId);
  }
}
