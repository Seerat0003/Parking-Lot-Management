package com.parkinglot.parkinglot.repository;

import com.parkinglot.parkinglot.model.User;
import com.parkinglot.parkinglot.model.Venue;
import com.parkinglot.parkinglot.model.VenueStatus;
import com.parkinglot.parkinglot.model.VenueType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VenueRepository extends JpaRepository<Venue, Long> {

    List<Venue> findByOwner(User owner);

    Optional<Venue> findByOwnerAndId(User owner, Long id);

    Page<Venue> findByStatus(VenueStatus status, Pageable pageable);

    @Query("""
            select v from Venue v
            where v.status = :status
            and (:city is null or lower(v.city) like lower(concat('%', :city, '%')))
            and (:name is null or lower(v.name) like lower(concat('%', :name, '%')))
            and (:type is null or v.type = :type)
            """)
    Page<Venue> searchPublished(@Param("status") VenueStatus status,
                                @Param("city") String city,
                                @Param("name") String name,
                                @Param("type") VenueType type,
                                Pageable pageable);
}
