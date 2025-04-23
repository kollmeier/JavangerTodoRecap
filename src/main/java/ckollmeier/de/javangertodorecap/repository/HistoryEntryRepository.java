package ckollmeier.de.javangertodorecap.repository;

import ckollmeier.de.javangertodorecap.entity.HistoryEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HistoryEntryRepository extends MongoRepository<HistoryEntry<?>, String> {
    Optional<HistoryEntry<?>> findFirstByUndoneAtIsNullOrderByDoneAtDesc();
    Optional<HistoryEntry<?>> findFirstByUndoneAtIsNotNullOrderByDoneAtAsc();
    Optional<HistoryEntry<?>> findFirstByEntityIdOrderByDoneAtDesc(String entityId);
}
