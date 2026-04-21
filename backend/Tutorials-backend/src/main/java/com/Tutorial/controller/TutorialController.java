package com.Tutorial.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.Tutorial.models.Tutorial;
import com.Tutorial.repository.TutorialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api")
public class TutorialController {

  @Autowired
  TutorialRepository tutorialRepository;

  @GetMapping("/tutorials")
  public ResponseEntity<List<Tutorial>> getAllTutorials(@RequestParam(required = false) String title) {
    try {
      List<Tutorial> tutorials = new ArrayList<>();

      if (title == null)
        tutorialRepository.findAll().forEach(tutorials::add);
      else
        tutorialRepository.findByTitleContaining(title).forEach(tutorials::add);

      if (tutorials.isEmpty()) {
        return ResponseEntity.noContent().build();
      }

      return ResponseEntity.ok(tutorials);

    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @GetMapping("/tutorials/{id}")
  public ResponseEntity<Tutorial> getTutorialById(@PathVariable("id") String id) {
    Optional<Tutorial> tutorialData = tutorialRepository.findById(id);

    return tutorialData
        .map(tutorial -> ResponseEntity.ok(tutorial))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PostMapping("/tutorials")
  public ResponseEntity<Tutorial> createTutorial(@RequestBody Tutorial tutorial) {
    try {
      Tutorial _tutorial = tutorialRepository.save(
          new Tutorial(tutorial.getTitle(), tutorial.getDescription(), tutorial.isPublished()));

      return ResponseEntity.status(HttpStatus.CREATED).body(_tutorial);

    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @PutMapping("/tutorials/{id}")
  public ResponseEntity<Tutorial> updateTutorial(@PathVariable("id") String id, @RequestBody Tutorial tutorial) {
    Optional<Tutorial> tutorialData = tutorialRepository.findById(id);

    if (tutorialData.isPresent()) {
      Tutorial _tutorial = tutorialData.get();
      _tutorial.setTitle(tutorial.getTitle());
      _tutorial.setDescription(tutorial.getDescription());
      _tutorial.setPublished(tutorial.isPublished());

      return ResponseEntity.ok(tutorialRepository.save(_tutorial));
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @DeleteMapping("/tutorials/{id}")
  public ResponseEntity<Void> deleteTutorial(@PathVariable("id") String id) {
    try {
      tutorialRepository.deleteById(id);
      return ResponseEntity.noContent().build();

    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @DeleteMapping("/tutorials")
  public ResponseEntity<Void> deleteAllTutorials() {
    try {
      tutorialRepository.deleteAll();
      return ResponseEntity.noContent().build();

    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @GetMapping("/tutorials/published")
  public ResponseEntity<List<Tutorial>> findByPublished() {
    try {
      List<Tutorial> tutorials = tutorialRepository.findByPublished(true);

      if (tutorials.isEmpty()) {
        return ResponseEntity.noContent().build();
      }

      return ResponseEntity.ok(tutorials);

    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }
}
