package com.bikeit.restendpoint.controller;

import com.bikeit.restendpoint.model.Dto.MapDto;
import com.bikeit.restendpoint.model.Map;
import com.bikeit.restendpoint.model.User;
import com.bikeit.restendpoint.service.MapService;
import com.bikeit.restendpoint.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MapController {
    @Autowired
    private MapService mapService;

    @Autowired
    private UserService userService;

    @GetMapping("/map/all")
    public ResponseEntity<List<Map>> getAllMaps() {
        try {
            String username = userService.getCurrentUsername();
            User currentUser = userService.findByUsername(username);
            List<Map> maps = mapService.getMaps(currentUser);
            return ResponseEntity.ok().body(maps);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        }
    }

    @PostMapping("/map")
    public ResponseEntity<Map> createMap(@RequestBody MapDto mapDto) {
        try {
            String username = userService.getCurrentUsername();
            User currentUser = userService.findByUsername(username);
            Map map = mapService.uploadMap(mapDto, currentUser);
            return ResponseEntity.ok().body(map);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        }
    }

    @DeleteMapping("/map/{id}")
    public ResponseEntity<Void> deleteMap(@PathVariable Long id) {
        try {
            String username = userService.getCurrentUsername();
            User currentUser = userService.findByUsername(username);
            mapService.deleteMap(id, currentUser);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        }
    }
}
