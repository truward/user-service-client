package com.truward.orion.user.service.model;

import org.springframework.web.bind.annotation.*;

/**
 * Defines a contract for major RESTful operations on user service.
 *
 * @author Alexander Shabanov
 */
public interface UserRestService {

  @RequestMapping(value = "/account/{id}", method = RequestMethod.GET)
  @ResponseBody
  UserModel.UserAccount getAccountById(@PathVariable("id") long id);

  @RequestMapping(value = "/account/list", method = RequestMethod.POST)
  @ResponseBody
  UserModel.ListAccountsResponse getAccounts(@RequestBody UserModel.ListAccountsRequest request);

  @RequestMapping(value = "/account/lookup", method = RequestMethod.POST)
  @ResponseBody
  UserModel.AccountLookupResponse lookupAccount(@RequestBody UserModel.AccountLookupRequest request);

  @RequestMapping(value = "/account", method = RequestMethod.POST)
  @ResponseBody
  UserModel.RegisterAccountResponse registerAccount(@RequestBody UserModel.RegisterAccountRequest request);

  @RequestMapping(value = "/account", method = RequestMethod.PUT)
  @ResponseBody
  UserModel.UpdateAccountResponse updateAccount(@RequestBody UserModel.UpdateAccountRequest request);

  @RequestMapping(value = "/account/list", method = RequestMethod.DELETE)
  @ResponseBody
  UserModel.DeleteAccountsResponse deleteAccounts(@RequestBody UserModel.DeleteAccountsRequest request);

  @RequestMapping(value = "/account/check/presence", method = RequestMethod.POST)
  @ResponseBody
  UserModel.AccountPresenceResponse checkAccountPresence(@RequestBody UserModel.AccountPresenceRequest request);

  @RequestMapping(value = "/token/create", method = RequestMethod.POST)
  @ResponseBody
  UserModel.CreateInvitationTokensResponse createInvitationTokens(
      @RequestBody UserModel.CreateInvitationTokensRequest request);
}
