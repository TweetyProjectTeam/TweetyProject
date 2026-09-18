/*
 * This file is part of "TweetyProject", a collection of Java libraries for
 * logical aspects of artificial intelligence and knowledge representation.
 *
 * TweetyProject is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3 as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2025 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.web.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * @author Oleksandr Dzhychko
 */
@SpringBootTest
@AutoConfigureMockMvc
class RequestControllerSequenceExplanationTest {
    @Autowired
    private MockMvc mvc;

    private static Stream<Arguments> badRequestsBodies() {
        return Stream.of(Arguments.of("unit_timeout null",
                // language=JSON
                """
                        {
                          "email":  null,
                          "timeout": 10,
                          "unit_timeout": null,
                          "cmd": {
                            "type": "get_sequence_explanations",
                            "attacks": []
                          }
                        }
                        """), Arguments.of("cmd null",
                // language=JSON
                """
                        {
                          "email":  null,
                          "timeout": 10,
                          "unit_timeout": "s",
                          "cmd": null
                        }
                        """), Arguments.of("attacks null",
                // language=JSON
                """
                        {
                          "email":  null,
                          "timeout": 10,
                          "unit_timeout": "s",
                          "cmd": {
                            "type": "get_sequence_explanations",
                            "attacks": null
                          }
                        }
                        """), Arguments.of("attack null",
                // language=JSON
                """
                        {
                          "email":  null,
                          "timeout": 10,
                          "unit_timeout": "s",
                          "cmd": {
                            "type": "get_sequence_explanations",
                            "attacks": [null]
                          }
                        }
                        """), Arguments.of("attacker null",
                // language=JSON
                """
                        {
                          "email":  null,
                          "timeout": 10,
                          "unit_timeout": "s",
                          "cmd": {
                            "type": "get_sequence_explanations",
                            "attacks": [{
                              "attacker": null,
                              "attacked": "a"
                            }]
                          }
                        }
                        """), Arguments.of("attacked null",
                // language=JSON
                """
                        {
                          "email":  null,
                          "timeout": 10,
                          "unit_timeout": "s",
                          "cmd": {
                            "type": "get_sequence_explanations",
                            "attacks": [{
                              "attacker": "a",
                              "attacked": null
                            }]
                          }
                        }
                        """), Arguments.of("argument null",
                // language=JSON
                """
                        {
                          "email":  null,
                          "timeout": 10,
                          "unit_timeout": "s",
                          "cmd": {
                            "type": "get_sequence_explanations",
                            "attacks": [],
                            "argument_filter": [null]
                          }
                        }
                        """));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("badRequestsBodies")
    public void sequenceExplanationBadRequest(String name, String requestBody) throws Exception {
        var post = post("/sequence-explanation").contentType(MediaType.APPLICATION_JSON).content(requestBody);

        mvc.perform(post).andExpect(status().isBadRequest());
    }

    @Test
    public void sequenceExplanationsForAllArguments() throws Exception {
        var post = post("/sequence-explanation").contentType(MediaType.APPLICATION_JSON)
                // language=JSON
                .content("""
                        {
                          "email":  "aId",
                          "timeout": 10,
                          "unit_timeout": "s",
                          "cmd": {
                            "type": "get_sequence_explanations",
                            "attacks": [
                              {
                                "attacker": "a",
                                "attacked": "b"
                              },
                              {
                                "attacker": "b",
                                "attacked": "a"
                              }
                            ]
                          }
                        }
                        """);

        mvc.perform(post).andExpect(status().isOk())
                // language=JSON
                .andExpect(content().json("""
                        {
                          "reply": {
                            "type": "get_sequence_explanations",
                            "perArgumentSequenceExplanations": {
                              "a": [
                                {
                                  "argument": "a",
                                  "supporters": [
                                    [
                                      "a"
                                    ]
                                  ],
                                  "defeated": [
                                    [
                                      "b"
                                    ]
                                  ]
                                }
                              ],
                              "b": [
                                {
                                  "argument": "b",
                                  "supporters": [
                                    [
                                      "b"
                                    ]
                                  ],
                                  "defeated": [
                                    [
                                      "a"
                                    ]
                                  ]
                                }
                              ]
                            }
                          },
                          "email": "aId",
                          "unit_timeout": "s",
                          "status": "SUCCESS"
                        }
                        """, false));
    }

    @Test
    public void sequenceExplanationsForSelectedArguments() throws Exception {
        var post = post("/sequence-explanation").contentType(MediaType.APPLICATION_JSON)
                // language=JSON
                .content("""
                        {
                          "email":  "aId",
                          "timeout": 10,
                          "unit_timeout": "s",
                          "cmd": {
                            "type": "get_sequence_explanations",
                            "attacks": [
                              {
                                "attacker": "a",
                                "attacked": "b"
                              },
                              {
                                "attacker": "b",
                                "attacked": "a"
                              }
                            ],
                            "argument_filter": ["b"]
                          }
                        }
                        """);

        mvc.perform(post).andExpect(status().isOk())
                // language=JSON
                .andExpect(content().json("""
                        {
                          "reply": {
                            "type": "get_sequence_explanations",
                            "perArgumentSequenceExplanations": {
                              "b": [
                                {
                                  "argument": "b",
                                  "supporters": [
                                    [
                                      "b"
                                    ]
                                  ],
                                  "defeated": [
                                    [
                                      "a"
                                    ]
                                  ]
                                }
                              ]
                            }
                          },
                          "email": "aId",
                          "unit_timeout": "s",
                          "status": "SUCCESS"
                        }
                        """, false));
    }

    @Test
    public void sequenceExplanationsForSelectedArgumentThatIsNotInAttackes() throws Exception {
        var post = post("/sequence-explanation").contentType(MediaType.APPLICATION_JSON)
                // language=JSON
                .content("""
                        {
                          "email":  "aId",
                          "timeout": 10,
                          "unit_timeout": "s",
                          "cmd": {
                            "type": "get_sequence_explanations",
                            "attacks": [],
                            "argument_filter": ["a"]
                          }
                        }
                        """);

        mvc.perform(post).andExpect(status().isOk())
                // language=JSON
                .andExpect(content().json("""
                        {
                          "reply": {
                            "type": "get_sequence_explanations",
                            "perArgumentSequenceExplanations": {
                              "a": [
                                {
                                  "argument": "a",
                                  "supporters": [
                                    [
                                      "a"
                                    ]
                                  ],
                                  "defeated": [
                                    []
                                  ]
                                }
                              ]
                            }
                          },
                          "email": "aId",
                          "unit_timeout": "s",
                          "status": "SUCCESS"
                        }
                        """, false));
    }
}