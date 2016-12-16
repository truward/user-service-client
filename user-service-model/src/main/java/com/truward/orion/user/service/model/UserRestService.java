package com.truward.orion.user.service.model;

import org.springframework.web.bind.annotation.*;

import static com.truward.orion.user.service.model.UserModelV1.*;

/**
 * Defines a contract for major RESTful operations on user service.
 *
 * @author Alexander Shabanov
 */
public interface UserRestService {

  @RequestMapping(value = "/account/{id}", method = RequestMethod.GET)
  @ResponseBody
  UserAccount getAccountById(@PathVariable("id") long id);

  @RequestMapping(value = "/account/list", method = RequestMethod.POST)
  @ResponseBody
  ListAccountsResponse getAccounts(@RequestBody ListAccountsRequest request);

  @RequestMapping(value = "/account/lookup", method = RequestMethod.POST)
  @ResponseBody
  AccountLookupResponse lookupAccount(@RequestBody AccountLookupRequest request);

  @RequestMapping(value = "/account", method = RequestMethod.POST)
  @ResponseBody
  RegisterAccountResponse registerAccount(@RequestBody RegisterAccountRequest request);

  @RequestMapping(value = "/account", method = RequestMethod.PUT)
  @ResponseBody
  UpdateAccountResponse updateAccount(@RequestBody UpdateAccountRequest request);

  @RequestMapping(value = "/account/list", method = RequestMethod.DELETE)
  @ResponseBody
  DeleteAccountsResponse deleteAccounts(@RequestBody DeleteAccountsRequest request);

  @RequestMapping(value = "/account/check/presence", method = RequestMethod.POST)
  @ResponseBody
  AccountPresenceResponse checkAccountPresence(@RequestBody AccountPresenceRequest request);

  @RequestMapping(value = "/token/create", method = RequestMethod.POST)
  @ResponseBody
  CreateInvitationTokensResponse createInvitationTokens(@RequestBody CreateInvitationTokensRequest request);
}
