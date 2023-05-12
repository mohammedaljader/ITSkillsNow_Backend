package com.itskillsnow.jobservice.service;

import com.itskillsnow.jobservice.dto.request.FavoriteJob.AddJobToFavoritesDto;
import com.itskillsnow.jobservice.dto.response.FavoriteJobView;
import com.itskillsnow.jobservice.dto.response.JobView;
import com.itskillsnow.jobservice.exception.FavoriteJobNotFoundException;
import com.itskillsnow.jobservice.exception.JobNotFoundException;
import com.itskillsnow.jobservice.exception.UserNotFoundException;
import com.itskillsnow.jobservice.model.FavoriteJob;
import com.itskillsnow.jobservice.model.Job;
import com.itskillsnow.jobservice.model.User;
import com.itskillsnow.jobservice.repository.FavoriteJobRepository;
import com.itskillsnow.jobservice.repository.JobRepository;
import com.itskillsnow.jobservice.repository.UserRepository;
import com.itskillsnow.jobservice.service.interfaces.FavoriteJobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class FavoriteJobServiceImpl implements FavoriteJobService {
    private final UserRepository userRepository;

    private final JobRepository jobRepository;

    private final FavoriteJobRepository favoriteJobRepository;


    @Override
    public Boolean addJobToFavorites(AddJobToFavoritesDto jobToFavoritesDto) {
        Job job = jobRepository.findById(jobToFavoritesDto.getJobId())
                .orElseThrow(() -> new JobNotFoundException("Job was not found!"));

        User user = userRepository.findByUsername(jobToFavoritesDto.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User was not found!"));

        FavoriteJob favoriteJob = mapAddFavoriteToModel(user, job);
        favoriteJobRepository.save(favoriteJob);
        return true;
    }

    @Override
    public Boolean deleteJobFromFavorites(UUID favoriteId) {
        FavoriteJob favoriteJob = favoriteJobRepository.findById(favoriteId)
                .orElseThrow(() -> new FavoriteJobNotFoundException("Favorite job was not found"));
        favoriteJobRepository.delete(favoriteJob);
        return true;
    }

    @Override
    public FavoriteJobView getFavoriteById(UUID favoriteId) {
        FavoriteJob favoriteJob = favoriteJobRepository.findById(favoriteId)
                .orElseThrow(() -> new FavoriteJobNotFoundException("Favorite job was not found"));
        return mapFavoriteJobToDto(favoriteJob);
    }

    @Override
    public List<FavoriteJobView> getAllFavoritesByUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User was not found!"));
        List<FavoriteJob> favoriteJobs = favoriteJobRepository.findAllByUser(user);
        return favoriteJobs.stream()
                .map(this::mapFavoriteJobToDto)
                .toList();
    }

    @Override
    public Integer getFavoritesNumberByJob(UUID jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException("Job was not found!"));
        return favoriteJobRepository.countAllByJob(job);
    }

    private FavoriteJob mapAddFavoriteToModel(User user, Job job){
        return FavoriteJob.builder()
                .favoriteDate(LocalDate.now())
                .favoriteTime(LocalTime.now())
                .user(user)
                .job(job)
                .build();
    }

    private FavoriteJobView mapFavoriteJobToDto(FavoriteJob favoriteJob){
        JobView jobView = mapJobModelToDto(favoriteJob.getJob());
        return FavoriteJobView.builder()
                .favoriteId(favoriteJob.getFavoriteId())
                .favoriteDate(favoriteJob.getFavoriteDate())
                .favoriteTime(favoriteJob.getFavoriteTime())
                .jobView(jobView)
                .build();
    }

    private JobView mapJobModelToDto(Job job){
        return JobView.builder()
                .jobId(job.getJobId())
                .jobName(job.getJobName())
                .jobDescription(job.getJobDescription())
                .jobImage(job.getJobImage())
                .jobAddress(job.getJobAddress())
                .jobCategory(job.getJobCategory())
                .jobEducationLevel(job.getJobEducationLevel())
                .jobType(job.getJobType())
                .jobHours(job.getJobHours())
                .username(job.getUser().getUsername())
                .build();
    }
}
