package ckollmeier.de.javangertodorecap.repository;

import ckollmeier.de.javangertodorecap.entity.Todo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TodoRepository extends MongoRepository<Todo, String> {
    List<Todo> findAllByOrderByCreatedAtDesc();
}
