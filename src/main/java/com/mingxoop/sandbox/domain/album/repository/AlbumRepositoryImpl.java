package com.mingxoop.sandbox.domain.album.repository;

import com.mingxoop.sandbox.domain.album.repository.query.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

import static com.mingxoop.sandbox.domain.album.repository.entity.QAlbumArtistEntity.albumArtistEntity;
import static com.mingxoop.sandbox.domain.album.repository.entity.QAlbumEntity.albumEntity;
import static com.mingxoop.sandbox.domain.album.repository.entity.QAlbumReviewStatsEntity.albumReviewStatsEntity;
import static com.mingxoop.sandbox.domain.album.repository.entity.QTrackArtistEntity.trackArtistEntity;
import static com.mingxoop.sandbox.domain.album.repository.entity.QTrackEntity.trackEntity;

@Repository
@RequiredArgsConstructor
public class AlbumRepositoryImpl implements AlbumRepository {

    private final JPAQueryFactory query;

    @Override
    public List<AlbumQuery> findAllByReviewCountCursorPaging(String cursorId, Long cursorReviewCount, long limit) {
        // 앨범 + 앨범 리뷰 통계 페이징
        List<AlbumQuery> albums = getAlbumWithReviewStats(cursorId, cursorReviewCount, limit);

        // 앨범 ID 추출
        List<String> albumIds = albums.stream()
                .map(AlbumQuery::getId)
                .toList();

        // 앨범 아티스트
        Map<String, List<ArtistQuery>> albumArtistsMap = getAlbumArtists(albumIds);
        Map<String, List<TrackQuery>> albumTracksMap = getAlbumTracks(albumIds);

        // Join + Transform
        return albums.stream()
                .peek(album -> {
                    album.setArtists(albumArtistsMap.getOrDefault(album.getId(), new ArrayList<>()));
                    album.setTracks(albumTracksMap.getOrDefault(album.getId(), new ArrayList<>()));
                })
                .toList();
    }

    private List<AlbumQuery> getAlbumWithReviewStats(String cursorId, Long cursorReviewCount, long limit) {
        // 앨범 + 앨범 리뷰 통계를 커서 페이지네이션 적용 후 반환
        return query
                .select(new QAlbumQuery(
                        albumEntity.id,
                        albumEntity.name,
                        albumEntity.type,
                        albumEntity.imageUrl,
                        albumEntity.releasedAt,
                        albumEntity.createdAt,
                        albumEntity.modifiedAt,
                        new QAlbumReviewStatsQuery(
                                albumReviewStatsEntity.id,
                                albumReviewStatsEntity.totalReviewer,
                                albumReviewStatsEntity.totalScore,
                                albumReviewStatsEntity.averageScore,
                                albumReviewStatsEntity.score1Count,
                                albumReviewStatsEntity.score2Count,
                                albumReviewStatsEntity.score3Count,
                                albumReviewStatsEntity.score4Count,
                                albumReviewStatsEntity.score5Count,
                                albumReviewStatsEntity.maleCount,
                                albumReviewStatsEntity.maleTotalScore,
                                albumReviewStatsEntity.femaleCount,
                                albumReviewStatsEntity.femaleTotalScore,
                                albumReviewStatsEntity.createdAt,
                                albumReviewStatsEntity.modifiedAt
                        ))
                )
                .from(albumEntity)
                .leftJoin(albumReviewStatsEntity).on(albumReviewStatsEntity.album.eq(albumEntity))
                .where(cursorCondition(cursorId, cursorReviewCount))
                .orderBy(albumReviewStatsEntity.totalReviewer.desc(), albumReviewStatsEntity.id.desc())
                .limit(limit)
                .fetch();
    }

    private BooleanExpression cursorCondition(String cursorId, Long cursorReviewCount) {
        if (cursorId == null || cursorReviewCount == null) {
            return null;
        }

        return albumReviewStatsEntity.totalReviewer.lt(cursorReviewCount)
                .or(albumReviewStatsEntity.totalReviewer.eq(cursorReviewCount)
                        .and(albumReviewStatsEntity.id.lt(cursorId)));
    }

    private Map<String, List<ArtistQuery>> getAlbumArtists(List<String> albumIds) {
        if (albumIds.isEmpty()) {
            return new HashMap<>();
        }

        // 앨범 ID 기반으로 아티스트 조회
        List<AlbumArtistQuery> results = query
                .select(new QAlbumArtistQuery(
                        albumEntity.id,
                        new QArtistQuery(
                                albumArtistEntity.artist.id,
                                albumArtistEntity.artist.name
                        )
                ))
                .from(albumEntity)
                .join(albumArtistEntity).on(albumArtistEntity.album.eq(albumEntity))
                .join(albumArtistEntity.artist)
                .where(albumEntity.id.in(albumIds))
                .fetch();

        // 앨범 ID를 기준으로 그룹핑 후 반환
        return results.stream()
                .collect(Collectors.groupingBy(
                        AlbumArtistQuery::getAlbumId,
                        Collectors.mapping(
                                AlbumArtistQuery::getArtist,
                                Collectors.toList()
                        )
                ));
    }
    private Map<String, List<TrackQuery>> getAlbumTracks(List<String> albumIds) {
        if (albumIds.isEmpty()) {
            return new HashMap<>();
        }
        // 앨범 ID 기반으로 트랙 조회
        List<AlbumTrackQuery> tracks = query
                .select(new QAlbumTrackQuery(
                        albumEntity.id,
                        new QTrackQuery(
                                trackEntity.id,
                                trackEntity.name,
                                trackEntity.trackNumber,
                                trackEntity.duration,
                                trackEntity.isPlayable,
                                trackEntity.previewUrl,
                                trackEntity.createdAt,
                                trackEntity.modifiedAt
                        )
                ))
                .from(trackEntity)
                .join(albumEntity).on(trackEntity.album.eq(albumEntity))
                .where(albumEntity.id.in(albumIds))
                .orderBy(trackEntity.trackNumber.asc())
                .fetch();

        // 트랙 ID 추출
        List<String> trackIds = tracks.stream()
                .map(AlbumTrackQuery::getTrack)
                .filter(Objects::nonNull)
                .map(TrackQuery::getId)
                .toList();

        Map<String, List<ArtistQuery>> trackArtistsMap = getTrackArtists(trackIds);

        // 트랙에 아티스트 정보 설정 후 앨범별로 그룹핑
        return tracks.stream()
                .peek(t -> {
                    TrackQuery track = t.getTrack();
                    track.setArtists(trackArtistsMap.getOrDefault(track.getId(), new ArrayList<>()));
                })
                .collect(Collectors.groupingBy(
                        AlbumTrackQuery::getAlbumId,
                        Collectors.mapping(
                                AlbumTrackQuery::getTrack,
                                Collectors.toList()
                        )
                ));
    }

    private Map<String, List<ArtistQuery>> getTrackArtists(List<String> trackIds) {
        if(trackIds.isEmpty()) {
            return new HashMap<>();
        }

        // 트랙 ID 기반으로 아티스트 조회
        List<TrackArtistQuery> results = query
                .select(new QTrackArtistQuery(
                        trackEntity.id,
                        new QArtistQuery(
                                trackArtistEntity.artist.id,
                                trackArtistEntity.artist.name
                        )
                ))
                .from(trackEntity)
                .join(trackArtistEntity).on(trackArtistEntity.track.eq(trackEntity))
                .join(trackArtistEntity.artist)
                .where(trackEntity.id.in(trackIds))
                .fetch();

        // 앨범 ID를 기준으로 그룹핑 후 반환
        return results.stream()
                .collect(Collectors.groupingBy(
                        TrackArtistQuery::getTrackId,
                        Collectors.mapping(
                                TrackArtistQuery::getArtist,
                                Collectors.toList()
                        )
                ));
    }
}






