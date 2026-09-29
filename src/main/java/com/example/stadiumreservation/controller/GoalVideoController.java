package com.example.stadiumreservation.controller;

import com.example.stadiumreservation.entity.GoalVideo;
import com.example.stadiumreservation.entity.GoalVote;
import com.example.stadiumreservation.repository.GoalVideoRepository;
import com.example.stadiumreservation.repository.GoalVoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/goal-videos")
@CrossOrigin(origins = "*")
public class GoalVideoController {

    @Autowired
    private GoalVideoRepository goalVideoRepository;

    @Autowired
    private GoalVoteRepository goalVoteRepository;

    // 1. Top 10 Videonu gətirmək
    @GetMapping("/top10")
    public ResponseEntity<List<GoalVideo>> getTop10Videos() {
        return ResponseEntity.ok(goalVideoRepository.findTop10ByOrderByVoteCountDesc());
    }

    // 2. Yeni Video Əlavə Etmək (Owner üçün)
    @PostMapping("/add")
    public ResponseEntity<GoalVideo> addVideo(@RequestBody GoalVideo video) {
        GoalVideo savedVideo = goalVideoRepository.save(video);
        return ResponseEntity.ok(savedVideo);
    }

    // 3. Səs Vermə Endpoint-i
    @PostMapping("/{videoId}/vote")
    public ResponseEntity<?> voteVideo(@PathVariable Long videoId, @RequestParam Long userId) {

        // Eyni istifadəçi artıq səs veribmi yoxlayırıq
        if (goalVoteRepository.existsByUserIdAndVideoId(userId, videoId)) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Siz artıq bu videoya səs vermisiniz!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Videonu tapıb səs sayını 1 artırırıq
        return goalVideoRepository.findById(videoId).map(video -> {
            video.setVoteCount(video.getVoteCount() + 1);
            GoalVideo updatedVideo = goalVideoRepository.save(video);

            // Səs qeydini goal_votes cədvəlinə yazırıq
            GoalVote vote = GoalVote.builder()
                    .userId(userId)
                    .videoId(videoId)
                    .build();
            goalVoteRepository.save(vote);

            return ResponseEntity.ok(updatedVideo); // Yenilənmiş voteCount ilə videonu qaytarırıq
        }).orElse(ResponseEntity.notFound().build());
    }
}