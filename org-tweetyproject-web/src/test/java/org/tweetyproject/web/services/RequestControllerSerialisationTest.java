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
 * Copyright 2026 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.web.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * @author Lars Bengel
 */
@SpringBootTest
@AutoConfigureMockMvc
class RequestControllerSerialisationTest {
    @Autowired
    private MockMvc mvc;

    @Test
    public void getInfos() throws Exception {
        var post = post("/serialisation").contentType(MediaType.APPLICATION_JSON)
                // language=JSON
                .content("""
                        {
                          "cmd": "info"
                        }
                        """);

        mvc.perform(post).andExpect(status().isOk())
                // language=JSON
                .andExpect(content().json("""
                        {
                          "reply": "info",
                          "email": null,
                          "backend_timeout": 600,
                          "selectionFunctions": [
                            "ADM","UC","GR"
                          ],
                          "terminationFunctions": [
                            "ADM","CO","UC","PR","ST"
                          ],
                          "commands": [
                            "get_reduct",
                            "is_terminal",
                            "get_selection",
                            "get_models",
                            "get_sequences"
                          ]
                        }
                        """, true));
    }

    @Test
    public void getReduct() throws Exception {
        var post = post("/serialisation").contentType(MediaType.APPLICATION_JSON)
                // language=JSON
                .content("""
                         {
                           "cmd": "get_reduct",
                           "nr_of_arguments": 4,
                           "attacks": [[1, 2],[2,3],[3,4],[4,3]],
                           "extension": [1],
                           "timeout": 10,
                           "unit_timeout": "s"
                        }
                        """);

        mvc.perform(post).andExpect(status().isOk())
                // language=JSON
                .andExpect(content().json("""
                        {
                          "reply": "get_reduct",
                          "email": null,
                          "nr_of_arguments": 4,
                          "attacks": [
                            [
                              1,
                              2
                            ],
                            [
                              2,
                              3
                            ],
                            [
                              3,
                              4
                            ],
                            [
                              4,
                              3
                            ]
                          ],
                          "extension": [1],
                          "selectionFunction": null,
                          "terminationFunction": null,
                          "answer": "<{ 3, 4 },[(3,4), (4,3)]>",
                          "time": 0,
                          "unit_time": "s",
                          "status": "SUCCESS"
                        }
                        """, true));
    }

    @Test
    public void getSelection() throws Exception {
        var post = post("/serialisation").contentType(MediaType.APPLICATION_JSON)
                // language=JSON
                .content("""
                         {
                           "cmd": "get_selection",
                           "nr_of_arguments": 4,
                           "attacks": [[1, 2],[2,3],[3,4],[4,3]],
                           "selectionFunction": "UC",
                           "timeout": 10,
                           "unit_timeout": "s"
                        }
                        """);

        mvc.perform(post).andExpect(status().isOk())
                // language=JSON
                .andExpect(content().json("""
                        {
                          "reply": "get_selection",
                          "email": null,
                          "nr_of_arguments": 4,
                          "attacks": [
                            [
                              1,
                              2
                            ],
                            [
                              2,
                              3
                            ],
                            [
                              3,
                              4
                            ],
                            [
                              4,
                              3
                            ]
                          ],
                          "extension": null,
                          "selectionFunction": "UC",
                          "terminationFunction": null,
                          "answer": "[{1}, {4}]",
                          "time": 0,
                          "unit_time": "s",
                          "status": "SUCCESS"
                        }
                        """, true));
    }

    @Test
    public void isTerminal() throws Exception {
        var post = post("/serialisation").contentType(MediaType.APPLICATION_JSON)
                // language=JSON
                .content("""
                         {
                           "cmd": "is_terminal",
                           "nr_of_arguments": 4,
                           "attacks": [[1, 2],[2,3],[3,4],[4,3]],
                           "terminationFunction": "UC",
                           "timeout": 10,
                           "unit_timeout": "s"
                        }
                        """);

        mvc.perform(post).andExpect(status().isOk())
                // language=JSON
                .andExpect(content().json("""
                        {
                          "reply": "is_terminal",
                          "email": null,
                          "nr_of_arguments": 4,
                          "attacks": [
                            [
                              1,
                              2
                            ],
                            [
                              2,
                              3
                            ],
                            [
                              3,
                              4
                            ],
                            [
                              4,
                              3
                            ]
                          ],
                          "extension": null,
                          "selectionFunction": null,
                          "terminationFunction": "UC",
                          "answer": "false",
                          "time": 0,
                          "unit_time": "s",
                          "status": "SUCCESS"
                        }
                        """, true));
    }

    @Test
    public void getModels() throws Exception {
        var post = post("/serialisation").contentType(MediaType.APPLICATION_JSON)
                // language=JSON
                .content("""
                         {
                           "cmd": "get_models",
                           "nr_of_arguments": 4,
                           "attacks": [[1, 2],[2,3],[3,4],[4,3]],
                           "selectionFunction": "ADM",
                           "terminationFunction": "PR",
                           "timeout": 10,
                           "unit_timeout": "s"
                        }
                        """);

        mvc.perform(post).andExpect(status().isOk())
                // language=JSON
                .andExpect(content().json("""
                        {
                          "reply": "get_models",
                          "email": null,
                          "nr_of_arguments": 4,
                          "attacks": [
                            [
                              1,
                              2
                            ],
                            [
                              2,
                              3
                            ],
                            [
                              3,
                              4
                            ],
                            [
                              4,
                              3
                            ]
                          ],
                          "extension": null,
                          "selectionFunction": "ADM",
                          "terminationFunction": "PR",
                          "answer": "[{1,3}, {1,4}]",
                          "time": 0,
                          "unit_time": "s",
                          "status": "SUCCESS"
                        }
                        """, true));
    }

    @Test
    public void getSequences() throws Exception {
        var post = post("/serialisation").contentType(MediaType.APPLICATION_JSON)
                // language=JSON
                .content("""
                         {
                           "cmd": "get_sequences",
                           "nr_of_arguments": 4,
                           "attacks": [[1, 2],[2,3],[3,4],[4,3]],
                           "selectionFunction": "UC",
                           "terminationFunction": "UC",
                           "timeout": 10,
                           "unit_timeout": "s"
                        }
                        """);

        mvc.perform(post).andExpect(status().isOk())
                // language=JSON
                .andExpect(content().json("""
                        {
                          "reply": "get_sequences",
                          "email": null,
                          "nr_of_arguments": 4,
                          "attacks": [
                            [
                              1,
                              2
                            ],
                            [
                              2,
                              3
                            ],
                            [
                              3,
                              4
                            ],
                            [
                              4,
                              3
                            ]
                          ],
                          "extension": null,
                          "selectionFunction": "UC",
                          "terminationFunction": "UC",
                          "answer": "[({4},{1}), ({1})]",
                          "time": 0,
                          "unit_time": "s",
                          "status": "SUCCESS"
                        }
                        """, true));
    }
}