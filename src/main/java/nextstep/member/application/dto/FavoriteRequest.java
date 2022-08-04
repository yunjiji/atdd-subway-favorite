package nextstep.member.application.dto;

public class FavoriteRequest {

    private Long source;
    private Long target;

    private FavoriteRequest() { }

    public Long getSource() {
        return source;
    }

    public Long getTarget() {
        return target;
    }
}