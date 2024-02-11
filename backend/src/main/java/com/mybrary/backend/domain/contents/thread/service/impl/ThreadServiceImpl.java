package com.mybrary.backend.domain.contents.thread.service.impl;

import com.mybrary.backend.domain.book.entity.Book;
import com.mybrary.backend.domain.book.repository.BookRepository;
import com.mybrary.backend.domain.comment.repository.CommentRepository;
import com.mybrary.backend.domain.contents.like.repository.LikeRepository;
import com.mybrary.backend.domain.contents.like.service.LikeService;
import com.mybrary.backend.domain.contents.paper.dto.requestDto.PaperUpdateDto;
import com.mybrary.backend.domain.contents.paper.dto.requestDto.PostPaperDto;
import com.mybrary.backend.domain.contents.paper.dto.responseDto.GetFollowingPaperDto;
import com.mybrary.backend.domain.contents.paper.dto.responseDto.PaperGetDto;
import com.mybrary.backend.domain.contents.paper.entity.Paper;
import com.mybrary.backend.domain.contents.paper.repository.PaperRepository;
import com.mybrary.backend.domain.contents.paper_image.entity.PaperImage;
import com.mybrary.backend.domain.contents.paper_image.repository.PaperImageRepository;
import com.mybrary.backend.domain.contents.scrap.entity.Scrap;
import com.mybrary.backend.domain.contents.scrap.repository.ScrapRepository;
import com.mybrary.backend.domain.contents.tag.entity.Tag;
import com.mybrary.backend.domain.contents.tag.repository.TagRepository;
import com.mybrary.backend.domain.contents.tag.service.TagService;
import com.mybrary.backend.domain.contents.thread.dto.requestDto.ThreadPostDto;
import com.mybrary.backend.domain.contents.thread.dto.requestDto.ThreadUpdateDto;
import com.mybrary.backend.domain.contents.thread.dto.responseDto.GetThreadDto;
import com.mybrary.backend.domain.contents.thread.dto.responseDto.ThreadGetDto;
import com.mybrary.backend.domain.contents.thread.dto.responseDto.ThreadInfoGetDto;
import com.mybrary.backend.domain.contents.thread.entity.Thread;
import com.mybrary.backend.domain.contents.thread.repository.ThreadRepository;
import com.mybrary.backend.domain.contents.thread.service.ThreadService;
import com.mybrary.backend.domain.image.entity.Image;
import com.mybrary.backend.domain.image.repository.ImageRepository;
import com.mybrary.backend.domain.image.service.ImageService;
import com.mybrary.backend.domain.member.dto.responseDto.MemberInfoDto;
import com.mybrary.backend.domain.member.entity.Member;
import com.mybrary.backend.domain.member.repository.MemberRepository;
import com.mybrary.backend.domain.mybrary.repository.MybraryRepository;
import com.mybrary.backend.domain.notification.service.NotificationService;
import com.mybrary.backend.global.exception.book.BookNotFoundException;
import com.mybrary.backend.global.exception.image.ImageNotFoundException;
import com.mybrary.backend.global.exception.member.EmailNotFoundException;
import com.mybrary.backend.global.exception.member.MemberNotFoundException;
import com.mybrary.backend.global.exception.paper.PaperListNotFoundException;
import com.mybrary.backend.global.exception.thread.MainThreadListNotFoundException;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class ThreadServiceImpl implements ThreadService {

      private final ThreadRepository threadRepository;
      private final ImageRepository imageRepository;
      private final ImageService imageService;
      private final PaperRepository paperRepository;
      private final PaperImageRepository paperImageRepository;
      private final MybraryRepository mybraryRepository;
      private final TagService tagService;
      private final TagRepository tagRepository;
      private final MemberRepository memberRepository;
      private final BookRepository bookRepository;
      private final ScrapRepository scrapRepository;
      private final LikeService likeService;
      private final LikeRepository likeRepository;
      private final CommentRepository commentRepository;
      private final NotificationService notificationService;

      /* 예외 처리 상황별로 추후 추가할예정 */

      @Transactional
      @Override
      public Long createThread(Long myId, ThreadPostDto threadPostDto) {
            log.info("createThread 살행");
            Thread thread = Thread.builder()
                                  .mybrary(mybraryRepository.searchByMemberId(myId))
                                  .build();
            threadRepository.save(thread);
            log.info("sp1");
            /* paper 객체 하나씩 생성하고 저장 */
            List<PostPaperDto> postPaperDtoList = threadPostDto.getPostPaperDto();
            log.info("sp2");
            // bookId가 null이 아닐 때
            // book의 마지막 페이지 번호
            int paperSeq = 0;   // 페이퍼 순서
            if (threadPostDto.getBookId() != null) {
                  paperSeq = scrapRepository.findLastPaperSeq(threadPostDto.getBookId()).orElse(0);
            }
            Member member = memberRepository.findById(myId)
                                            .orElseThrow(NullPointerException::new);
            log.info("memberId = {} / email = {} / nickname = {}", member.getId(), member.getEmail(), member.getNickname());
            for (PostPaperDto dto : postPaperDtoList) {
                  /* paper 객체 생성 */
                  Paper paper = Paper.builder()
                                     .member(member)
                                     .thread(thread)
                                     .layoutType(dto.getLayoutType())
                                     .content1(dto.getContent1())
                                     .content2(dto.getContent2())
                                     .isPaperPublic(threadPostDto.isPaperPublic())
                                     .isScrapEnabled(threadPostDto.isScrapEnable())
                                     .build();
                  paperRepository.save(paper);
                  /* paperImage 객체 생성 */
                  Long imageId1 = dto.getImageId1();
                  Long imageId2 = dto.getImageId2();
                  if (imageId1 != null) {
                        Image image1 = imageRepository.findById(dto.getImageId1()).orElseThrow(ImageNotFoundException::new);
                        PaperImage paperImage1 = PaperImage.builder()
                                                           .paper(paper)
                                                           .image(image1)
                                                           .imageSeq(1)
                                                           .build();
                        paperImageRepository.save(paperImage1);
                  }
                  if(imageId2 != null){
                        Image image2 = imageRepository.findById(dto.getImageId2()).orElseThrow(ImageNotFoundException::new);
                        PaperImage paperImage2 = PaperImage.builder()
                                                           .paper(paper)
                                                           .image(image2)
                                                           .imageSeq(2)
                                                           .build();
                        paperImageRepository.save(paperImage2);
                  }
                  log.info("paper 저장 이후");

                  /* bookid가 있는경우에만 scrap 생성 */
                  if (threadPostDto.getBookId() != null) {
                        Book book = bookRepository.findById(threadPostDto.getBookId())
                                                  .orElseThrow(BookNotFoundException::new);
                        Scrap scrap = Scrap.builder()
                                           .paper(paper)
                                           .book(book)
                                           .paperSeq(++paperSeq)
                                           .build();
                        scrapRepository.save(scrap);
                  }
                  /* tag 목록 생성 */
                  List<String> tagNameList = dto.getTagList();
                  List<Tag> tagEntityList = new ArrayList<>();
                  if(!tagNameList.isEmpty()){
                        for (String tagNames : tagNameList) {
                              /* paperId, tag명 */
                              Tag tag = Tag.builder()
                                           .tagName(tagNames)
                                           .paper(paper)
                                           .build();
                              tagEntityList.add(tag);
                        }
                        tagRepository.saveAll(tagEntityList);
                        log.info("tag목록 생성 이후 ");
                  }

                  /* 여기서 페이퍼에 대한 멘션 알림 보내는 로직 */
                  /* 쓰레드를 생성한 멤버가 sender, 멘션된 회원이 receiver, 알람타입은  */
//                  List<Long> mentionedIdList = dto.getMentionList();
//                  for (Long mentiondedId : mentionedIdList) {
//                        NotificationPostDto mentionNotificationPostDto =
//                            NotificationPostDto.builder()
//                                               .notifyType(2)
//                                               .senderId(member.getId())
//                                               .receiverId(mentiondedId)
//                                               .build();
//                        notificationService.saveNotification(mentionNotificationPostDto);
//                  }


            } /* paper생성 for문 끝 */

            return thread.getId();
      }



      /* 메인 피드 thread 조회하기 */
      @Transactional
      @Override
      public List<GetThreadDto> getMainAllThread(Long myId, Pageable pageable) {
//        /* following중인 멤버(본인 포함) 의 쓰레드 최대 5개와 관련된 정보 dto 생성 */
//        List<GetThreadDto> threadDtoList = threadRepository
//            .getFollowingThreadDtoResults(myId, pageable);
//        /* following중이지 않은 멤버의 쓰레드 최대 10개 조회와 관련 정보 dto 생성*/
//        int getRandomCount = 10 - threadDtoList.size();
//        threadDtoList.addAll(
//            threadRepository.getRandomThreadDtoResults(myId, pageable, getRandomCount));
//        /* list 내에서 무작위로 순서 배정 */
//        Collections.shuffle(threadDtoList);
//
//        /* followingThreadDtos의 각 threadId에 해당하는 paper관련 정보 조회 */
//        for (GetThreadDto threadDto : threadDtoList) {
//            /* threadId에 해당하는 paper 관련 정보 dto 목록 조회 */
//            List<GetFollowingPaperDto> getFollowingPaperDtoList =
//                paperRepository.getFollowingPaperDtoResults(threadDto.getThreadId());
//            /* 페이퍼 관련정보 처리 로직 */
//            for (GetFollowingPaperDto paperDto : getFollowingPaperDtoList) {
//                /* 좋아요 여부 판단, 태그목록 포함 처리, 이미지 url들 포함 처리*/
//                List<String> imageUrls = imageRepository.findByPaperId(paperDto.getId());
//
//                paperDto = GetFollowingPaperDto.builder()
//                                               .isLiked(likeService.checkIsLiked(paperDto.getId(), myId))
//                                               .tagList(tagService.getTagNameList(paperDto.getId()))
//                                               .imageUrl1(imageUrls.get(0))
//                                               .imageUrl2(imageUrls.get(1))
//                                               .build();
//            }
//
//
//        }

            /* following중인 멤버(본인 포함) 의 쓰레드 최대 5개와 관련된 정보 dto 생성 */
            List<GetThreadDto> threadDtoList = threadRepository.getFollowingThreadDtoResults(myId, pageable).orElseThrow(
                MainThreadListNotFoundException::new);
            System.out.println("1");
            System.out.println("크기" + threadDtoList.size());
            /* following중이지 않은 멤버의 쓰레드 최대 10개 조회와 관련 정보 dto 생성*/
            int getRandomCount = 10 - threadDtoList.size();
            threadDtoList.addAll(
                threadRepository.getRandomThreadDtoResults(myId, pageable, getRandomCount).orElseThrow(MainThreadListNotFoundException::new));
            System.out.println("2");
            System.out.println("크기" + threadDtoList.size());
            /* list 내에서 무작위로 순서 배정 */
            Collections.shuffle(threadDtoList);
            /* followingThreadDtos의 각 threadId에 해당하는 paper관련 정보 조회 */
            for (int i = 0;i<threadDtoList.size();i++) {
                  GetThreadDto threadDto = threadDtoList.get(i);
                  /* threadId에 해당하는 paper 관련 정보 dto 목록 조회 */
                  List<GetFollowingPaperDto> getFollowingPaperDtoList =
                      paperRepository.getFollowingPaperDtoResults(threadDto.getThreadId()).orElseThrow(PaperListNotFoundException::new);
                  System.out.println("3");

                  /* 페이퍼 관련정보 처리 로직 */
                  for (int j = 0;j<getFollowingPaperDtoList.size();j++) {
                        GetFollowingPaperDto paperDto = getFollowingPaperDtoList.get(j);

                        /* 좋아요 여부 판단, 태그목록 포함 처리, 이미지 url들 포함 처리*/
                        List<Long> imageUrls = imageRepository.findPaperImage(paperDto.getId()).orElseThrow(ImageNotFoundException::new);
                        System.out.println("4");

                        paperDto.setImageId1(imageUrls.get(0));
                        paperDto.setImageUrl1(imageRepository.findById(imageUrls.get(0)).orElse(null).getUrl());
                        if(imageUrls.size()==2) {
                              paperDto.setImageId2(imageUrls.get(1));
                              paperDto.setImageUrl2(imageRepository.findById(imageUrls.get(1)).orElse(null).getUrl());

                        }

                        paperDto.setLikesCount(likeRepository.getLikeCount(paperDto.getId()).orElse(0));
                        paperDto.setCommentCount(commentRepository.getCommentCount(paperDto.getId()).orElse(0));
                        paperDto.setScrapCount(scrapRepository.getScrapCount(paperDto.getId()).orElse(0));
                        paperDto.setLiked(likeService.checkIsLiked(paperDto.getId(), myId));
                        paperDto.setTagList(tagRepository.getTagList(paperDto.getId()).orElse(new ArrayList<>()));
                        System.out.println("5");

                        paperDto.setBookList(bookRepository.getBookForMainThread(threadDto.getMemberId(), paperDto.getId()).orElse(new ArrayList<>()));
                  }

                  threadDto.setPaperList(getFollowingPaperDtoList);
                  threadDto.setPaperPublic(getFollowingPaperDtoList.get(0).isPaperPublic());
                  threadDto.setScrapEnable(getFollowingPaperDtoList.get(0).isScrapEnable());

            }
            return threadDtoList;

      }


      /* 나의 모든 thread들만 조회하기 */
      @Transactional
      @Override
      public List<ThreadInfoGetDto> getMyAllThread(Long myId, Pageable pageable) {
            Member member = memberRepository.findById(myId)
                                            .orElseThrow(NullPointerException::new);
            log.info("memeberId:" + myId);

            List<Thread> threadList = threadRepository.getThreadsByMemberId(myId, pageable)
                                                      .orElse(new ArrayList<>());

            List<ThreadInfoGetDto> threadInfoGetDtoList = new ArrayList<>();
            if(!threadList.isEmpty()){
                  for(Thread thread : threadList){
                        threadInfoGetDtoList.add(threadRepository.getSimpleThreadDtoResult(thread.getId()));
                  }
            }
            return threadInfoGetDtoList;
      }


      /* 특정 member의 모든 thread들만 조회하기 */
      @Transactional
      @Override
      public List<ThreadInfoGetDto> getOtherAllThread(Long memberId, Pageable pageable) {

            Member member = memberRepository.findById(memberId)
                                            .orElseThrow(NullPointerException::new);
            log.info("memeberId:" + memberId);

            List<Thread> threadList = threadRepository.getThreadsByMemberId(memberId, pageable)
                                                      .orElse(new ArrayList<>());

            List<ThreadInfoGetDto> threadInfoGetDtoList = new ArrayList<>();
            if(!threadList.isEmpty()){
                  for(Thread thread : threadList){
                        threadInfoGetDtoList.add(threadRepository.getSimpleThreadDtoResult(thread.getId()));
                  }
            }

            return threadInfoGetDtoList;
      }


      /* 쓰레드 아이디로 쓰레드 단건조회 */
      @Transactional
      @Override
      public ThreadGetDto getThread(Long memberId, Long threadId) {

            /* paperGetDtoList 정보 조회 및 생성 */
            List<PaperGetDto> paperGetDtoList = paperRepository.getPaperGetDto(threadId).orElseThrow(PaperListNotFoundException::new);

            /**/
            log.info("2");
            log.info("size: " + paperGetDtoList.size());
            for (PaperGetDto paperGetDto : paperGetDtoList) {
                  log.info("fd" + paperGetDto.getPaperId());
                  paperGetDto.updateIsLiked(likeService.checkIsLiked(paperGetDto.getPaperId(), memberId));
                  log.info("checkisliked");
                  List<Tag> tagList = tagRepository.getTagsByPaperId(paperGetDto.getPaperId())
                                                   .orElse(Collections.emptyList());
                  /**/
                  log.info("3");
                  List<String> tagNameList = new ArrayList<>();

                  if (!tagList.isEmpty()) {
                        tagNameList = tagList.stream()
                                             .map(Tag::getTagName)
                                             .toList();
                  }
                  paperGetDto.updateTagList(tagNameList);

                  /* image id와 url은 따로 */
                  Image image1 = imageRepository.findImage1ByPaperId(paperGetDto.getPaperId()).orElse(null);
                  Image image2 = imageRepository.findImage2ByPaperId(paperGetDto.getPaperId()).orElse(null);
                  paperGetDto.updateImageId1(image1.getId());
                  paperGetDto.updateImageId2(image2.getId());
                  paperGetDto.updateImageUrl1(image1.getUrl());
                  paperGetDto.updateImageUrl2(image2.getUrl());
            }
            /**/
            log.info("4");

            /* ThreadGetDto 생성 */

            Paper firstPaper = paperRepository.findById(paperGetDtoList.get(0).getPaperId())
                                              .orElseThrow(NullPointerException::new);
            MemberInfoDto memberInfoDto = memberRepository.getMemberInfo(memberId).orElseThrow(MemberNotFoundException::new);

            ThreadGetDto threadGetDto = ThreadGetDto.builder()
                                                    .threadId(threadId)
                                                    .member(memberInfoDto)
                                                    .paperList(paperGetDtoList)
                                                    .build();
            threadGetDto.updateIsPublicEnable(firstPaper.isPaperPublic());
            threadGetDto.updateIsScrapEnable(firstPaper.isScrapEnabled());

            return threadGetDto;
      }




      @Transactional
      @Override
      public Long updateThread(Long myId, ThreadUpdateDto threadUpdateDto) {
            Thread thread = threadRepository.findById(threadUpdateDto.getThreadId())
                                            .orElseThrow(NullPointerException::new);
            Member member = memberRepository.findById(myId)
                                            .orElseThrow(EmailNotFoundException::new);
            List<PaperUpdateDto> paperUpdateDtoList = threadUpdateDto.getPaperList();

            for (PaperUpdateDto paperDto : paperUpdateDtoList) {
                  Paper paper = paperRepository.findById(paperDto.getPaperId())
                                               .orElseThrow(NullPointerException::new);
                  Scrap scrap = scrapRepository.findByPaperId(paperDto.getPaperId());
                  Book book = bookRepository.findById(paperDto.getBookId()).orElseThrow(NullPointerException::new);
                  scrap.updateBook(book);

                  paper.updateLayoutType(paperDto.getLayoutType());
                  paper.updateContent1(paperDto.getContent1());
                  paper.updateContent2(paperDto.getContent2());
                  paper.updatePaperPublic(threadUpdateDto.isPaperPublic());
                  paper.updateScrapEnabled(threadUpdateDto.isScrapEnable());
                  List<String> tagNameList = paperDto.getTagList();

                  /* 기존 태그들 삭제 */
                  tagRepository.deleteAllByPaperId(paper.getId());

                  List<Tag> tagEntityList = new ArrayList<>();
                  for (String tagNames : tagNameList) {
                        /* paperId, tag명 */
                        Tag tag = Tag.builder()
                                     .tagName(tagNames)
                                     .paper(paper)
                                     .build();
                        tagEntityList.add(tag);
                  }
                  tagRepository.saveAll(tagEntityList);
            }
            return thread.getId();
      }


      @Transactional
      @Override
      public int deleteThread(Long myId, Long threadId) {
            /* 삭제된 페이퍼 개수 반환 */
            Thread thread = threadRepository.findById(threadId)
                                            .orElseThrow(NullPointerException::new);
            int count = thread.getPaperList().size();

//        paperRepository.deleteAll(thread.getPaperList());
            threadRepository.delete(thread);

            return count;
      }
}
