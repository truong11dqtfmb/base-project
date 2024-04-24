package com.dqt.security.utils;

import com.dqt.security.constant.Constant;
import com.dqt.security.entity.User;
import com.dqt.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class Utils {
    private final UserRepository userRepository;


    public static <T> T getRandomElement(List<T> list) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("List cannot be null or empty");
        }

        Random random = new Random();
        int randomIndex = random.nextInt(list.size());
        return list.get(randomIndex);
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    public static Map<String, Object> putData(int page, int pageSize, long total, Object data) {
        Map<String, Object> output = new HashMap<>();
        output.put("page", page);
        output.put("pageSize", pageSize);
        output.put("total", total);
        output.put("data", data);
        return output;
    }

    public static Double getPercent(long value, long total) {
        double pass = (double) value / total;
        return Math.round(pass * 10000.0) / 100.0;
    }

    public static Double formatDouble(Double value) {
        try {
            NumberFormat formatter = new DecimalFormat("#0.00");
            return Double.parseDouble(formatter.format(value));
        } catch (Exception e) {
            return 0d;
        }
    }

    public static boolean checkPhone(String phone) {
        String phoneRegex = "^[0-9]{10}$";
        Pattern pattern = Pattern.compile(phoneRegex);
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }

    public static boolean checkEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static Integer generateRandomInt(int num) {
        Random random = new Random();
        Integer number = random.nextInt(num) + 1;
        return number;
    }

    public static String removeAccent(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("");
    }

    public static boolean isStringInList(String input, List<String> stringList) {
        return stringList.stream()
                .anyMatch(str -> input.startsWith(str));
    }

    public User gerCurrentUser() {
        String currentUserName = null;
        User user = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            currentUserName = authentication.getName();
        }
        if (!currentUserName.isBlank()) {
            user = userRepository.findByEmail(currentUserName).get();
        }
        return user;
    }

    public boolean isHasRoleAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (Constant.ROLE_ADMIN.equals(authority.getAuthority()))
                return true;
        }
        return false;
    }

    public boolean isHasRoleUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (Constant.ROLE_USER.equals(authority.getAuthority()))
                return true;
        }
        return false;
    }

    public boolean isHasRoleAny() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (Constant.ROLE_ADMIN.equals(authority.getAuthority()) || Constant.ROLE_USER.equals(authority.getAuthority()))
                return true;
        }
        return false;
    }


}
