package com.petcarebackend.repository;

import com.petcarebackend.model.ActivityLog;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ActivityLogRepository extends MongoRepository<ActivityLog, String> {

    List<ActivityLog> findByPetIdOrderByRecordedAtDesc(Long petId);
}
