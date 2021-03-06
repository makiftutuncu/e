package dev.akif.espringexample;

import java.util.Collections;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dev.akif.espringexample.people.dto.PersonDTOWithId;
import e.gson.EGsonCodec;
import e.gson.EOrGsonCodec;
import e.java.E;
import e.java.EOr;

@SpringBootApplication(exclude = {JacksonAutoConfiguration.class})
@EnableWebMvc
@ComponentScan("dev.akif")
public class Main implements WebMvcConfigurer {
    private static final Gson gson = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(E.class, EGsonCodec.get())
            .registerTypeAdapter(EOr.class, new EOrGsonCodec<>(new Gson(), PersonDTOWithId.class))
            .create();

    @Bean
    public Gson gson() {
        return gson;
    }

    @Override public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        GsonHttpMessageConverter gsonHttpMessageConverter = new GsonHttpMessageConverter();
        gsonHttpMessageConverter.setGson(gson);
        gsonHttpMessageConverter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON));
        converters.add(gsonHttpMessageConverter);
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
