package com.mustafaberk.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mustafaberk.model.SymptomRule;

@Repository
public interface SymptomRuleRepository extends JpaRepository<SymptomRule, Long>{




    @Query(value = "SELECT r.* FROM symptom_rule r " +
                   "JOIN department_symptom_rule dr ON r.id = dr.symptom_rule_id " +
                   "WHERE dr.department_id = :departmentId", 
           nativeQuery = true)
    List<SymptomRule> findRulesByDepartmentId(@Param("departmentId") Long departmentId);
    
}
