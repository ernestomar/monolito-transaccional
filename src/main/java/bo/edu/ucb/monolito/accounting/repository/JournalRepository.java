package bo.edu.ucb.monolito.accounting.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import bo.edu.ucb.monolito.accounting.entity.Journal;

public interface JournalRepository extends JpaRepository<Journal, Integer> {
    
}
