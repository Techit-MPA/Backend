package com.springles.repository;

import com.springles.domain.entity.Player;
import org.springframework.data.repository.CrudRepository;

public interface PlayerRedisRepository extends CrudRepository<Player,String> {

}
