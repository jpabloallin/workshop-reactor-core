package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;

@Service
public class PersonService {
    private final BiFunction<PersonRepository, Person, Mono<Person>> validateBeforeInsert
            = (repo, person) -> repo.findByName(person.getName());
    @Autowired
    private PersonRepository repository;

    public Flux<Person> listAll() {
        return repository.findAll();
    }

    public Mono<Person> getPerson(String id) {
        return repository.findById(id);
    }

    public Mono<Person> update(Person person) {
        return repository.save(person);
    }

    public Mono<Void> insert(Mono<Person> personMono) {
        return personMono
                .flatMap(person -> validateBeforeInsert.apply(repository, person))
                .switchIfEmpty(Mono.defer(() -> personMono.doOnNext(repository::save)))
                .then();
    }
}
