package com.bikeit.restendpoint.service;

import com.bikeit.restendpoint.model.Dto.MapDto;
import com.bikeit.restendpoint.model.Map;
import com.bikeit.restendpoint.model.User;
import com.bikeit.restendpoint.repository.MapRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MapService {
    @Autowired
    private MapRepository mapRepository;

    public List<Map> getMaps(User user) {
        return mapRepository.findByUser(user);
    }

    public Map uploadMap(MapDto mapDto, User user) {
        Map map = new Map(mapDto.getImageBase64(), mapDto.getPoints(), user);
        mapRepository.save(map);
        return map;
    }

    public void deleteMap(Long mapId, User user) {
        Optional<Map> map = mapRepository.findById(mapId);
        if(map.isEmpty()) {throw new IllegalArgumentException("Map not found");}
        if(!map.get().getUser().equals(user)) {throw new IllegalArgumentException("User does not belong to this map");}
        mapRepository.deleteById(mapId);
    }
}
