package com.example.recipeProject.domain;

import org.springframework.data.repository.CrudRepository;

public interface ForgotFormRepository extends CrudRepository<ForgotForm, Long> {
	ForgotForm findByEmail(String email);
}
