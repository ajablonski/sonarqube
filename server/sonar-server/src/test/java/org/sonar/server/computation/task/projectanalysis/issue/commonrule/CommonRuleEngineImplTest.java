/*
 * SonarQube
 * Copyright (C) 2009-2018 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.computation.task.projectanalysis.issue.commonrule;

import java.util.Collection;
import org.junit.Test;
import org.sonar.core.issue.DefaultIssue;
import org.sonar.server.computation.task.projectanalysis.component.Component;
import org.sonar.server.computation.task.projectanalysis.component.FileAttributes;
import org.sonar.server.computation.task.projectanalysis.component.ReportComponent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.sonar.server.computation.task.projectanalysis.component.ReportComponent.DUMB_PROJECT;

public class CommonRuleEngineImplTest {

  CommonRule rule1 = mock(CommonRule.class);
  CommonRule rule2 = mock(CommonRule.class);
  CommonRuleEngineImpl underTest = new CommonRuleEngineImpl(rule1, rule2);

  @Test
  public void process_files_with_known_language() throws Exception {
    ReportComponent file = ReportComponent.builder(Component.Type.FILE, 1)
      .setKey("FILE_KEY").setUuid("FILE_UUID")
      .setFileAttributes(new FileAttributes(false, "java", 1))
      .build();
    DefaultIssue issue = new DefaultIssue();
    when(rule1.processFile(file, "java")).thenReturn(issue);
    when(rule2.processFile(file, "java")).thenReturn(null);

    Collection<DefaultIssue> issues = underTest.process(file);
    assertThat(issues).containsOnly(issue);
  }

  @Test
  public void do_not_process_files_with_unknown_language() throws Exception {
    ReportComponent file = ReportComponent.builder(Component.Type.FILE, 1)
      .setKey("FILE_KEY").setUuid("FILE_UUID")
      .setFileAttributes(new FileAttributes(false, null, 1))
      .build();

    Collection<DefaultIssue> issues = underTest.process(file);

    assertThat(issues).isEmpty();
    verifyZeroInteractions(rule1, rule2);
  }

  @Test
  public void do_not_process_non_files() throws Exception {
    Collection<DefaultIssue> issues = underTest.process(DUMB_PROJECT);

    assertThat(issues).isEmpty();
    verifyZeroInteractions(rule1, rule2);
  }
}
