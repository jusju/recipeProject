package com.example.recipeProject.domain;

import org.springframework.data.repository.CrudRepository;

public interface LoginRepository extends CrudRepository<Login, Long> {
	Login findByUsername(String username);
	Login findByEmail(String email);
}
