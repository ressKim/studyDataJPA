package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends
        JpaRepository<Member, Long>
        , MemberRepositoryCustom
        , JpaSpecificationExecutor<Member> {//JpaSpecificationExecutor<Member> - criteria 관련 - 쓰지 말자구한다..

    List<Member> findByUsernameAndAgeGreaterThan(String userName, int age);//길어지면 너무 길어진다

    List<Member> findTop3HelloBy();

    //    @Query(name = "Member.findByUsername")
    List<Member> findByUsername(@Param("username") String username);

    @Query("select m from Member m where m.username = :username and m.age = :age")
//복잡한걸 쿼리로 바로 표현할 수 있다.
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    @Query("select" +
            " new study.datajpa.dto.MemberDto(m.id, m.username, t.name)" +
            " from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") List<String> names);

    List<Member> findListByUsername(String userName);//컬렉션

    Member findMemberByUsername(String userName);//단건

    Optional<Member> findOptionalByUsername(String userName);//옵션단건

    //쿼리가 복잡해 질수록 count 하는데 많이 걸리기 때문에 따로 해주는게 성능 최적화 면에서 좋다.
    //핵심 비즈니스 로직만 짜면 나머지는 알아서 해주는 느낌이 있기때문에 좋다.
    @Query(value = "select m from Member m left join m.team t",
            countQuery = "select count(m.username) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);

    Slice<Member> findSliceByAge(int age, Pageable pageable);

    @Modifying(clearAutomatically = true)//이게 있어야 된다. - (clear 자동 실행해주기)
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    //fetch join 쓰는거라고 보면 된다.
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    @EntityGraph(attributePaths = {"team"})
//    @EntityGraph("Member.all") //Member 의 NamedEntityGraph 이용
    List<Member> findEntityGraphByUsername(@Param("username") String username);

    //readOnly 가 되면 변경감지도 안해서 바뀌지 않는다. - 최적화가 되긴 하는데 진짜 몇퍼센트 안된다는걸 알고있자. - 진짜 진짜 많은 api 등에만 넣는게 좋다
    //보통 성능 느리면 그전에 캐시등 써야된다.
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);

    //Projection 관련 - 이렇게 하면 구현체가 담겨서 나간다
//    List<UsernameOnly> findProjectionsByUsername(@Param("username") String username);
    <T> List<T> findProjectionsByUsername(@Param("username") String username, Class<T> type);

    @Query(value = "select * from member where username = ?", nativeQuery = true)
    Member findByNativeQuery(String username);

    @Query(value = "select m.member_id as id, m.username, t.name as teamName " +
            "from member m left join team t",
             countQuery = "select count(*) from member",
            nativeQuery = true)
    Page<MemberProjection> findByNativeProjection(Pageable pageable);

}
