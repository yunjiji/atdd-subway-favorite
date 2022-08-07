package nextstep.subway.applicaion;

import nextstep.auth.exception.AuthenticationException;
import nextstep.member.domain.LoginMember;
import nextstep.member.domain.Member;
import nextstep.member.domain.MemberRepository;
import nextstep.subway.applicaion.dto.FavoriteRequest;
import nextstep.subway.applicaion.dto.FavoriteResponse;
import nextstep.subway.domain.Favorite;
import nextstep.subway.domain.FavoriteRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.exception.NotExistElementException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class FavoriteService {
    private final StationRepository stationRepository;
    private final FavoriteRepository favoriteRepository;
    private final MemberRepository memberRepository;

    public FavoriteService(StationRepository stationRepository, FavoriteRepository favoriteRepository, MemberRepository memberRepository) {
        this.stationRepository = stationRepository;
        this.favoriteRepository = favoriteRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public FavoriteResponse saveFavorite(FavoriteRequest favoriteRequest, LoginMember loginMember) {
        Member member = getMember(loginMember);

        Station sourceStation  = getStation(favoriteRequest.getSource());
        Station targetStation  = getStation(favoriteRequest.getTarget());
        Favorite favorite = favoriteRepository.save(new Favorite(member, sourceStation, targetStation, LocalDateTime.now()));

        return FavoriteResponse.of(favorite);
    }

    public List<FavoriteResponse> getFavoriteList(LoginMember loginMember){
        System.out.println("get favorite 시작");
        Member member = getMember(loginMember);

        return favoriteRepository.findByMemberId(member.getId()).stream()
                .map(FavoriteResponse::of)
                .collect(Collectors.toList());

    }

    @Transactional
    public void deleteFavoriteById(LoginMember loginMember, Long id) {
        Member member = getMember(loginMember);
        Favorite favorite = favoriteRepository.findById(id)
                .orElseThrow(() -> new NotExistElementException("등록되어 있는 즐겨찾기 경로가 아닙니다."));
        if(!favorite.isValidAuth(member)){
            throw new AuthenticationException();
        }
        favoriteRepository.deleteById(id);
    }

    private Member getMember(LoginMember loginMember){
        return memberRepository.findByEmail(loginMember.getEmail())
                .orElseThrow(() -> new NotExistElementException("등록된 멤버를 찾을 수 없습니다."));
    }

    private Station getStation(Long StationId){
        return stationRepository.findById(StationId)
                .orElseThrow(() -> new NotExistElementException("등록된 지하철역으로 요청하셔야 합니다."));
    }

}