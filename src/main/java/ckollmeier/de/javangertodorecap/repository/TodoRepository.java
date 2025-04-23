package ckollmeier.de.javangertodorecap.repository;

import ckollmeier.de.javangertodorecap.entity.Todo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TodoRepository extends MongoRepository<Todo, String> {
}
