package io.github.mortuusars.envelope.util.bugger_data;

import io.github.mortuusars.envelope.util.bugger.Bugger;
import io.github.mortuusars.envelope.util.bugger.page.BuggerPage;
import net.minecraft.nbt.ListTag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EnvelopeBuggerPage implements BuggerPage {
    @Override
    public String getTitle() {
        return "Envelope";
    }

    @Override
    public List<String> getLeftLines() {
        return Bugger.MAIL_SERVICE.get()
              .map(tag -> {

                  List<String> lines = new ArrayList<>(List.of(
                        "Mailboxes: " + tag.getInt("mailboxes").orElse(0),
                        "",
                        "Mail:",
                        "  Dropped: " + tag.getInt("dropped_mail_count").orElse(0),
                        "  Awaiting Payback: " + tag.getInt("payback_pending_mail_count").orElse(0),
                        "",
                        "Couriers:",
                        "  Real: " + tag.getInt("delivering_pigeons").orElse(0),
                        "  Background: " + tag.getInt("background_delivering_pigeons").orElse(0),
                        "  Finished: " + tag.getInt("background_finished_pigeons").orElse(0)
                  ));

                  ListTag deliveries = tag.getListOrEmpty("deliveries");
                  if (!deliveries.isEmpty()) {
                      lines.add("");
                      lines.add("Deliveries:");
                      for (int i = 0; i < deliveries.size(); i++) {
                          deliveries.getString(i).ifPresent(delivery -> lines.add("  " + delivery));
                      }
                  }

                  return lines;
              })
              .orElse(Collections.emptyList());
    }
}
